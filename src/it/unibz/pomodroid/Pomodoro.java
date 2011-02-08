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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
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
	
	/**
	 * True is running
	 */
	private boolean isRunning = false;

	/**
	 * Handler to handle events such as the start of a Pomodoro, the tick of the
	 * timer, a Pomodoro broken, a finished Pomodoro.
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {

			switch (message.what) {
			case Pomodoro.MSG_POMODORO_START:
				if (getUser().isDimLight())
					setBrightness(DESIRED_BRIGHTNESS);
				updateTimeTask.run();
				setButtonsClickable(false, true);
				isRunning = true;
				break;
			case Pomodoro.MSG_POMODORO_TICK:
				pomodoroSecondsValue--;
				break;
			case Pomodoro.MSG_POMODORO_FINISHED:
				if (getUser().isDimLight())
					setBrightness(originalBrightness);
				pomodoroSecondsValue = getUser().getPomodoroMinutesDuration()
						* SECONDS_PER_MINUTE;
				it.unibz.pomodroid.persistency.Activity activity = null;
				updateTextViewPomodoroTimer(pomodoroSecondsValue);
				String pomodoroMessage = null;
				try {
					activity = it.unibz.pomodroid.persistency.Activity.get(
							activityOrigin, activityOriginId, getDbHelper());
					activity.addOnePomodoro();
					activity.save(getDbHelper());
					pomodoroMessage = getUser().isLongerBreak(getDbHelper()) ? getString(R.string.pomodoro_long_break)
							: getString(R.string.pomodoro_short_break);
					Integer numberPomodoro = activity.getNumberPomodoro();
					TextView textViewActivityNumberPomodoro = (TextView) findViewById(R.id.TextViewActivityNumberPomodoro);
					textViewActivityNumberPomodoro
							.setText("Number of pomodoro: "
									+ numberPomodoro.toString());
				} catch (PomodroidException e) {
					pomodoroMessage = "Something wrong: " + e.getMessage();
				}
				notifyUser(pomodoroMessage);
				isRunning = false;
				setButtonsClickable(true, false);
				break;
			case Pomodoro.MSG_POMODORO_STOP:
				if (getUser().isDimLight())
					setBrightness(originalBrightness);
				pomodoroSecondsValue = getUser().getPomodoroMinutesDuration()
						* SECONDS_PER_MINUTE;
				updateTextViewPomodoroTimer(pomodoroSecondsValue);
				notifyUser(getString(R.string.pomodoro_broken));
				isRunning = false;
				setButtonsClickable(true, false);
				removeCallbacks(updateTimeTask);
				break;
			}
		}

	};

	/**
	 * a Runnable called every seconds, that is responsible to call the method
	 * to update the Pomodoro Timer View. It also sends messages to the handler.
	 */
	private Runnable updateTimeTask = new Runnable() {
		public void run() {
			updateTextViewPomodoroTimer(pomodoroSecondsValue);
			if (pomodoroSecondsValue == 0) {
				handler.removeCallbacks(this);
				handler.sendEmptyMessage(MSG_POMODORO_FINISHED);
				return;
			}
			handler.sendEmptyMessage(MSG_POMODORO_TICK);
			handler.postDelayed(this, MILLISECONDS_PER_SECOND);
		}
	};

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
		this.pomodoroSecondsValue = getUser().getPomodoroMinutesDuration()
				* SECONDS_PER_MINUTE;
		this.activityOrigin = this.getIntent().getExtras().getString("origin");
		this.activityOriginId = this.getIntent().getExtras().getInt("originId");

		it.unibz.pomodroid.persistency.Activity activity = null;
		try {
			activity = it.unibz.pomodroid.persistency.Activity.get(
					this.activityOrigin, this.activityOriginId, super.getDbHelper());
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
	public void onResume() {
		super.onResume();
		wakeLock.acquire();
	}

	@Override
	public void onPause() {
		super.onPause();
		wakeLock.release();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		it.unibz.pomodroid.persistency.Activity activity = null;
		ScrollView scrollView = (ScrollView) findViewById(R.id.ScrollView01);
		try {
			activity = it.unibz.pomodroid.persistency.Activity.get(
					this.activityOrigin, this.activityOriginId, super.getDbHelper());
			switch (v.getId()) {
			case R.id.ButtonPomodoroStart:
				Event eventStart = new Event("pomodoro", "start", new Date(),
						activity, pomodoroSecondsValue);
				eventStart.save(super.getDbHelper());
				handler.sendEmptyMessage(MSG_POMODORO_START);
				scrollView.fullScroll(ScrollView.FOCUS_UP);
				setButtonsClickable(false, true);
				break;
			case R.id.ButtonPomodoroStop:
				Event eventStop = new Event("pomodoro", "stop", new Date(),
						activity, pomodoroSecondsValue);
				eventStop.save(super.getDbHelper());
				handler.sendEmptyMessage(MSG_POMODORO_STOP);
				setButtonsClickable(true, false);
				break;
			}
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if (isRunning){
	    		String message = "Either Finish or Break the Pomodoro.";
	    		it.unibz.pomodroid.exceptions.PomodroidException.createAlert(this, "Info", message);
	    	}else{
	    		return super.onKeyDown(keyCode, event);
	    	}
	        return false;
	    } else {
	        return super.onKeyDown(keyCode, event);
	    }
	}

}