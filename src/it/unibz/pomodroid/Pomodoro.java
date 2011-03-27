/**
 * This file is part of Pomodroid.
 *
 *   Pomodroid is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Pomodroid is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Pomodroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibz.pomodroid;

import java.util.Date;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.User;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.KeyEvent;

/**
 * This class implements graphically the pomodoro technique. Here we have the
 * counter and the description of the activity to be faced.
 * 
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedActivity
 * @see android.view.View.OnClickListener;
 * @see http://www.pomodorotechnique.com
 */

public class Pomodoro extends SharedActivity implements OnClickListener {

	/**
	 * Default value when dimming light
	 */
	private final static float DESIRED_BRIGHTNESS = 0.05f;
	/**
	 * How many seconds are in a minute
	 */
	private final static int SECONDS_PER_MINUTE = 60;
	/**
	 * How many milliseconds are in a second
	 */
	private final static int MILLISECONDS_PER_SECOND = 1000;

	/**
	 * Dummy ID for our notifications
	 */
	private final static int NOTIFICATION_ID = 1;

	/**
	 * Sent when Pomodoro ticks a second
	 */
	private final static int MSG_POMODORO_TICK = 1;
	/**
	 * Sent when Pomodoro finishes
	 */
	private final static int MSG_POMODORO_FINISHED = 2;
	/**
	 * Sent when Pomodoro starts
	 */
	private final static int MSG_POMODORO_START = 3;
	/**
	 * Sent when Pomodoro is stopped
	 */
	private final static int MSG_POMODORO_STOP = 4;
	/**
	 * Holds remaining time in seconds
	 */
	private int pomodoroSecondsValue = 1;
	/**
	 * Stores the origin of the Activity
	 */
	private String activityOrigin = null;
	/**
	 * Stores the id of the Activity
	 */
	private int activityOriginId = -1;
	/**
	 * Wake variable for Power Manager
	 */
	private PowerManager.WakeLock wakeLock;
	/**
	 * Will store the brightness value set by user
	 */
	private float originalBrightness = -1;

	/** Messenger for communicating with service. */
	Messenger mService = null;
	/** Flag indicating whether we have called bind on the service. */
	boolean mIsBound;
	
	boolean isRunning = false;
		
	/**
	 * Handler of incoming messages from service.
	 */
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PomodoroService.MSG_POMODORO_TICK:
				pomodoroSecondsValue = msg.getData().getInt(
						"pomodoroSecondsValue");
				updateTextViewPomodoroTimer(pomodoroSecondsValue);
				break;
			case PomodoroService.MSG_POMODORO_FINISHED:
				if (getUser().isDimLight())
					setBrightness(originalBrightness);
				pomodoroSecondsValue = msg.getData().getInt(
				"pomodoroSecondsValue");
				isRunning = false;
				updateTextViewPomodoroTimer(pomodoroSecondsValue);
				setButtonsClickable(true, false);
				
				Integer numberPomodoro = msg.getData().getInt("numberPomodoro");
				String pomodoroMessage = msg.getData().getString("pomodoroMessage");
				TextView textViewActivityNumberPomodoro = (TextView) findViewById(R.id.TextViewActivityNumberPomodoro);
				textViewActivityNumberPomodoro
						.setText("Number of pomodoro: "
								+ numberPomodoro.toString());
				notifyUser(pomodoroMessage);
				
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	private IncomingHandler handler = new IncomingHandler();

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(handler);

	/**
	 * Class for interacting with the main interface of the service.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. We are communicating with our
			// service through an IDL interface, so get a client-side
			// representation of that from the raw service object.
			mService = new Messenger(service);

			// We want to monitor the service for as long as we are
			// connected to it.
			try {
				Message msg = Message.obtain(null,
						PomodoroService.MSG_REGISTER_CLIENT);
				msg.replyTo = mMessenger;
				mService.send(msg);
			} catch (RemoteException e) {
				// In this case the service has crashed before we could even
				// do anything with it; we can count on soon being
				// disconnected (and then reconnected if it can be restarted)
				// so there is no need to do anything here.
			}

			Log.i("Pomodoro", "connected to service");
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			mService = null;

			Log.i("Pomodoro", "disconnected from service");
		}
	};

	void doBindService() {
		// Establish a connection with the service. We use an explicit
		// class name because there is no reason to be able to let other
		// applications replace our component.
		bindService(new Intent(Pomodoro.this, PomodoroService.class),
				mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
		Log.i("Pomodoro", "binded to service");
	}

	void doUnbindService() {
		if (mIsBound) {
			// If we have received the service, and hence registered with
			// it, then now is the time to unregister.
			if (mService != null) {
				try {
					Message msg = Message.obtain(null,
							PomodoroService.MSG_UNREGISTER_CLIENT);
					msg.replyTo = mMessenger;
					mService.send(msg);
				} catch (RemoteException e) {
					// There is nothing special we need to do if the service
					// has crashed.
				}
			}

			// Detach our existing connection.
			unbindService(mConnection);
			mIsBound = false;
		}
	}

	private void sendMessenger(int message) {
		Message msg = Message.obtain(null, message, 0, 0);
		try {
			mService.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the buttons clickable or not
	 * 
	 * @param buttonPomodoroStartClickable
	 *            The Start Button
	 * @param buttonPomodoroStopClickable
	 *            The Stop Button
	 */
	private void setButtonsClickable(boolean buttonPomodoroStartClickable,
			boolean buttonPomodoroStopClickable) {
		Button buttonPomodoroStart = (Button) findViewById(R.id.ButtonPomodoroStart);
		Button buttonPomodoroStop = (Button) findViewById(R.id.ButtonPomodoroStop);
		buttonPomodoroStart.setClickable(buttonPomodoroStartClickable);
		buttonPomodoroStop.setClickable(buttonPomodoroStopClickable);
	}

	/**
	 * Updates the Timer TextView, given the new timer value in seconds
	 * 
	 * @param pomodoroSecondsValue
	 */
	private void updateTextViewPomodoroTimer(int pomodoroSecondsValue) {
		TextView textViewPomodoroTimer = (TextView) findViewById(R.id.TextViewPomodoroTimer);

		int seconds = pomodoroSecondsValue;
		int minutes = seconds / SECONDS_PER_MINUTE;
		seconds = seconds % SECONDS_PER_MINUTE;

		if (seconds >= 10 && seconds < 60) {
			if (minutes < 10)
				textViewPomodoroTimer.setText("0" + minutes + ":" + seconds);
			else
				textViewPomodoroTimer.setText(minutes + ":" + seconds);
		} else if (seconds < 10) {
			if (minutes < 10)
				textViewPomodoroTimer.setText("0" + minutes + ":0" + seconds);
			else
				textViewPomodoroTimer.setText(minutes + ":0" + seconds);
		} else {
			textViewPomodoroTimer.setText("" + minutes + ":" + seconds);
		}
	}

	/**
	 * Notifies the user in various ways
	 * 
	 * @param message
	 *            The message we want to display to the user
	 */
	private void notifyUser(String message) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.pomo_red,
				"Pomodroid", System.currentTimeMillis());
		Intent intent = new Intent(context, Pomodoro.class);
		Bundle bundle = new Bundle();
		bundle.putString("origin", activityOrigin);
		bundle.putInt("originId", activityOriginId);
		TextView textViewActivitySummary = (TextView) findViewById(R.id.TextViewActivitySummary);
		String activitySummary = (String) textViewActivitySummary.getText();
		intent.putExtras(bundle);
		notification.setLatestEventInfo(Pomodoro.this, activitySummary,
				message, PendingIntent.getActivity(getBaseContext(), 0, intent,
						PendingIntent.FLAG_CANCEL_CURRENT));
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.ledARGB = 0xff00ff00;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		if (getUser().isVibration())
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		if (getUser().isNotifications())
			nm.notify(NOTIFICATION_ID, notification);

		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Sets the device brightness
	 * 
	 * @param brightness
	 */
	private void setBrightness(float brightness) {
		WindowManager.LayoutParams layoutParameters = getWindow()
				.getAttributes();
		layoutParameters.screenBrightness = brightness;
		getWindow().setAttributes(layoutParameters);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibz.pomodroid.SharedActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pomodoro);

		this.pomodoroSecondsValue = 10; // FIXME
										// getUser().getPomodoroMinutesDuration()
										// * SECONDS_PER_MINUTE;
		this.activityOrigin = this.getIntent().getExtras().getString("origin");
		this.activityOriginId = this.getIntent().getExtras().getInt("originId");

		it.unibz.pomodroid.persistency.Activity activity = null;
		try {
			activity = it.unibz.pomodroid.persistency.Activity.get(
					this.activityOrigin, this.activityOriginId,
					super.getDbHelper());
			this.setUser(User.retrieve(super.getDbHelper()));
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
		updateTextViewPomodoroTimer(pomodoroSecondsValue);

		Button buttonPomodoroStart = (Button) findViewById(R.id.ButtonPomodoroStart);
		Button buttonPomodoroStop = (Button) findViewById(R.id.ButtonPomodoroStop);
		buttonPomodoroStart.setOnClickListener((OnClickListener) this);
		buttonPomodoroStop.setOnClickListener((OnClickListener) this);
		buttonPomodoroStop.setClickable(false);

		TextView textViewActivitySummary = (TextView) findViewById(R.id.TextViewActivitySummary);
		textViewActivitySummary.setText(activity.getSummary() + " ("
				+ activity.getStringDeadline() + ")");
		TextView textViewActivityDescription = (TextView) findViewById(R.id.TextViewActivityDescription);
		textViewActivityDescription.setText(activity.getDescription()
				+ " (Given by: " + activity.getReporter() + ")");
		TextView textViewActivityNumberPomodoro = (TextView) findViewById(R.id.TextViewActivityNumberPomodoro);
		Integer numberPomodoro = activity.getNumberPomodoro();
		textViewActivityNumberPomodoro.setText("Number of pomodoro: "
				+ numberPomodoro.toString());

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"DoNotDimScreen");

		this.originalBrightness = getWindow().getAttributes().screenBrightness;
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to LocalService
		Intent intent = new Intent(this, PomodoroService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		final Intent pomodoroService = new Intent(context,
				PomodoroService.class);
		pomodoroService.putExtra("origin", this.activityOrigin);
		pomodoroService.putExtra("originId", this.activityOriginId);
		startService(pomodoroService);
	}

	@Override
	public void onResume() {
		super.onResume();
		wakeLock.acquire();
	}

	@Override
	public void onPause() {
		super.onPause();
		wakeLock.release();
		if (isRunning) {
			String message = "Pomodroid running in background...";
			it.unibz.pomodroid.exceptions.PomodroidException.createAlert(
						this, "Info", message);
		}
	}

	@Override
	public void onClick(View v) {
		ScrollView scrollView = (ScrollView) findViewById(R.id.ScrollView01);
			switch (v.getId()) {
			case R.id.ButtonPomodoroStart:
				if (getUser().isDimLight())
					setBrightness(originalBrightness);
				sendMessenger(PomodoroService.MSG_POMODORO_START);
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				isRunning = true;
				setButtonsClickable(false, true);
				break;
			case R.id.ButtonPomodoroStop:
				sendMessenger(PomodoroService.MSG_POMODORO_STOP);
				if (getUser().isDimLight())
					setBrightness(originalBrightness);
				sendMessenger(PomodoroService.MSG_POMODORO_STOP);
				pomodoroSecondsValue = 10; // FIXME:
											// getUser().getPomodoroMinutesDuration()
											// * SECONDS_PER_MINUTE;
				updateTextViewPomodoroTimer(pomodoroSecondsValue);
				notifyUser(getString(R.string.pomodoro_broken));
				isRunning = false;
				setButtonsClickable(true, false);
				break;
			}
	}

}