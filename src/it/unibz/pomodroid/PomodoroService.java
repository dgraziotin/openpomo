package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.User;
import java.util.Date;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * this is the service that is hooked by the main activity - File Scanner this
 * will read the params and do something
 * 
 * @author sschauhan
 * 
 */

public class PomodoroService extends Service {

	/**
	 * How many seconds are in a minute
	 */
	private final static int SECONDS_PER_MINUTE = 60;
	/**
	 * How many milliseconds are in a second
	 */
	public final static int MILLISECONDS_PER_SECOND = 1000;
	/**
	 * Sent when Pomodoro ticks a second
	 */
	public final static int MSG_REGISTER_CLIENT = -1;
	/**
	 * Sent when Pomodoro ticks a second
	 */
	public final static int MSG_POMODORO_TICK = 1;
	/**
	 * Sent when Pomodoro finishes
	 */
	public final static int MSG_POMODORO_FINISHED = 2;
	/**
	 * Sent when Pomodoro starts
	 */
	public final static int MSG_POMODORO_START = 3;
	/**
	 * Sent when Pomodoro is stopped
	 */
	public final static int MSG_POMODORO_STOP = 4;
	/**
	 * Sent when Pomodoro ticks a second
	 */
	public final static int MSG_UNREGISTER_CLIENT = -2;
	/**
	 * Holds remaining time in seconds
	 */
	private int pomodoroSecondsValue = -1;
	/**
	 * Stores the origin of the Activity
	 */
	private String activityOrigin = null;
	/**
	 * Stores the id of the Activity
	 */
	private int activityOriginId = -1;
	/**
	 * The Database container for db4o
	 */
	private DBHelper dbHelper;
	/**
	 * The actual User
	 */
	private User user;
	/**
	 * For communication with the Service
	 */
	private Messenger messenger;
	/**
	 * @return dbHelper object
	 */
	public DBHelper getDbHelper() {
		return dbHelper;
	}

	/**
	 * @return user object
	 */
	public User getUser() {
		try {
			return User.retrieve(dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
		return null;
	}

	/**
	 * The Context of the Activity
	 */
	protected Context context = null;

	/**
	 * Activated when the bind with Service is completed
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	/**
	 * Handler of incoming messages from clients.
	 */
	class IncomingHandler extends Handler {
		Intent intent;
		Bundle data = new Bundle();

		@Override
		public void handleMessage(Message message) {
			it.unibz.pomodroid.persistency.Activity activity = null;
			try {
				activity = it.unibz.pomodroid.persistency.Activity.get(
						PomodoroService.this.activityOrigin,
						PomodoroService.this.activityOriginId,
						PomodoroService.this.getDbHelper());

				switch (message.what) {
				case PomodoroService.MSG_REGISTER_CLIENT:
					PomodoroService.this.messenger = message.replyTo;
					break;
				case PomodoroService.MSG_UNREGISTER_CLIENT:
					PomodoroService.this.messenger = null;
					break;
				case PomodoroService.MSG_POMODORO_START:
					pomodoroSecondsValue = getUser().getPomodoroMinutesDuration() * SECONDS_PER_MINUTE;
					Event eventStart = new Event("pomodoro", "start",
							new Date(), activity, pomodoroSecondsValue);
					eventStart.save(PomodoroService.this.getDbHelper());
					updateTimeTask.run();
					break;
				case PomodoroService.MSG_POMODORO_TICK:
					pomodoroSecondsValue--;
					data.putInt("pomodoroSecondsValue", pomodoroSecondsValue);
					communicateService(PomodoroService.MSG_POMODORO_TICK, data);
					break;
				case PomodoroService.MSG_POMODORO_FINISHED:
					Event eventFinish = new Event("pomodoro", "finish", new Date(),
							activity, pomodoroSecondsValue);
					eventFinish.save(PomodoroService.this.getDbHelper());
					activity.addOnePomodoro();
					activity.save(getDbHelper());
					pomodoroSecondsValue = getUser().getPomodoroMinutesDuration() * SECONDS_PER_MINUTE;
					
					String pomodoroMessage = getUser().isLongerBreak(getDbHelper()) ? getString(R.string.pomodoro_long_break)
							: getString(R.string.pomodoro_short_break);
					
					data.putInt("numberPomodoro", activity.getNumberPomodoro());
					data.putString("pomodoroMessage", pomodoroMessage);
					data.putString("origin", PomodoroService.this.activityOrigin);
					data.putInt("originId", PomodoroService.this.activityOriginId);
					communicateService(PomodoroService.MSG_POMODORO_FINISHED, data);
					break;
				case PomodoroService.MSG_POMODORO_STOP:
					Event eventStop = new Event("pomodoro", "stop", new Date(),
							activity, pomodoroSecondsValue);
					eventStop.save(PomodoroService.this.getDbHelper());
					pomodoroSecondsValue = getUser().getPomodoroMinutesDuration() * SECONDS_PER_MINUTE;
					removeCallbacks(updateTimeTask);
					break;
				}
			} catch (PomodroidException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Holds the custom handler 
	 */
	private IncomingHandler handler = new IncomingHandler();

	
	/**
	 * For contacting the Service with the Messenger
	 * @param message
	 * @param data
	 */
	private void communicateService(int message, Bundle data) {
		Message msg = Message.obtain(null, message, 0, 0);
		if (data != null)
			msg.setData(data);
		try {
			messenger.send(msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Target we publish for clients to send messages to IncomingHandler.
	 */
	final Messenger mMessenger = new Messenger(handler);

	/**
	 * a Runnable called every seconds, that is responsible to call the method
	 * to update the Pomodoro Timer View. It also sends messages to the handler.
	 */
	private Runnable updateTimeTask = new Runnable() {
		public void run() {
			if (pomodoroSecondsValue == 0) {
				handler.removeCallbacks(this);
				handler.sendEmptyMessage(MSG_POMODORO_FINISHED);
				return;
			}
			handler.sendEmptyMessage(MSG_POMODORO_TICK);
			handler.postDelayed(this, MILLISECONDS_PER_SECOND);
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		this.dbHelper = new DBHelper(getApplicationContext());
		this.user = getUser();
		pomodoroSecondsValue = getUser().getPomodoroMinutesDuration() * SECONDS_PER_MINUTE;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		handleIntent(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleIntent(intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	public void handleIntent(Intent intent) {
		this.activityOrigin = intent.getExtras().getString("origin");
		this.activityOriginId = intent.getExtras().getInt("originId");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
