package it.unibz.pomodroid;

import java.util.Date;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.persistency.DBHelper;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Pomodoro extends Activity implements OnClickListener {
	public final static int SUCCESS_RETURN_CODE = 1;
	private final static int SECONDS_PER_MINUTES = 60;
	private final static int MILLISECONDS_PER_SECONDS = 1000;
	private int pomodoroDurationMilliseconds;
	private TextView textViewPomodoroTimer = null;
	private TextView textViewActivitySummary = null;
	private TextView textViewActivityDeadline = null;
	private TextView textViewActivityDescription = null;
	private TextView textViewActivityNumberPomodoro = null;
	private TextView textViewActivityReporter = null;
	private Button buttonPomodoroStart = null;
	private Button buttonPomodoroStop = null;
	private CountDown counter = null;
	private DBHelper dbHelper = null;
	private String activityOrigin = null;
	private int activityOriginId = -1;
	private User user = null;
	private Context context = null;
	


	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pomodoro);
		this.dbHelper = new DBHelper(this);
		this.context = this;
		this.activityOrigin = this.getIntent().getExtras().getString("origin");
		this.activityOriginId = this.getIntent().getExtras().getInt("originId");
		it.unibz.pomodroid.persistency.Activity activity = null;
		try {
			activity = it.unibz.pomodroid.persistency.Activity
					.getActivity(this.activityOrigin, this.activityOriginId, this.dbHelper);
			this.user = User.retrieve(dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
		this.textViewPomodoroTimer = (TextView) findViewById(R.id.TextViewPomodoroTimer);
		this.buttonPomodoroStart = (Button) findViewById(R.id.ButtonPomodoroStart);
		this.buttonPomodoroStart.setOnClickListener((OnClickListener) this);
		this.buttonPomodoroStop = (Button) findViewById(R.id.ButtonPomodoroStop);
		this.buttonPomodoroStop.setOnClickListener((OnClickListener) this);
		this.buttonPomodoroStop.setClickable(false);

		this.textViewActivitySummary = (TextView) findViewById(R.id.TextViewActivitySummary);
		this.textViewActivityDeadline = (TextView) findViewById(R.id.TextViewActivityDeadline);
		this.textViewActivityDescription = (TextView) findViewById(R.id.TextViewActivityDescription);
		this.textViewActivityNumberPomodoro = (TextView) findViewById(R.id.TextViewActivityNumberPomodoro);
		this.textViewActivityReporter = (TextView) findViewById(R.id.TextViewActivityReporter);

		this.textViewActivitySummary.setText(activity.getSummary());
		this.textViewActivityDeadline.setText(activity.getDeadline()
				.toString());
		this.textViewActivityDescription
				.setText(activity.getDescription());
		Integer numberPomodoro = activity.getNumberPomodoro();
		this.textViewActivityNumberPomodoro.setText(numberPomodoro.toString());
		this.textViewActivityReporter.setText(activity.getReporter());

		//this.pomodoroDurationMilliseconds = user.getPomodoroMinutesDuration()
		//		* SECONDS_PER_MINUTES * MILLISECONDS_PER_SECONDS;
		this.pomodoroDurationMilliseconds = 10000; // FIXME: delete it before
													// production!
		counter = new CountDown(this.pomodoroDurationMilliseconds,
				MILLISECONDS_PER_SECONDS);
		this.textViewPomodoroTimer.setText(this
				.getFormattedTimerValue(this.pomodoroDurationMilliseconds));
	}

	@Override
	public void onPause() {
		super.onPause();
		this.dbHelper.close();
		Intent intent = new Intent();
		setResult(SUCCESS_RETURN_CODE, intent);
	}

	@Override
	public void onClick(View v) {
		it.unibz.pomodroid.persistency.Activity activity = null;
		
		try {
			activity = it.unibz.pomodroid.persistency.Activity
					.getActivity(this.activityOrigin, this.activityOriginId, this.dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
		
		switch (v.getId()) {
		case R.id.ButtonPomodoroStart:
			Event event = new Event("pomodoro","start",new Date(),activity,pomodoroDurationMilliseconds / 1000);
			try {
				event.save(dbHelper);
			} catch (PomodroidException e1) {
				try {
					throw new PomodroidException("ERROR! Could not create event! " + e1.getMessage());
				} catch (PomodroidException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			counter.start();
			this.buttonPomodoroStart.setClickable(false);
			this.buttonPomodoroStop.setClickable(true);
			break;
		case R.id.ButtonPomodoroStop:
			
			Event event1 = new Event("pomodoro","stop",new Date(),activity,counter.getCurrentCoundDownMilliseconds() / 1000);
			try {
				event1.save(dbHelper);
			} catch (PomodroidException e) {
				try {
					throw new PomodroidException("ERROR, could not save Event! " +e.getMessage());
				} catch (PomodroidException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			this.buttonPomodoroStart.setClickable(true);
			this.buttonPomodoroStop.setClickable(false);
			try {
				counter.stop();
			} catch (PomodroidException e) {
				e.alertUser(context, "INFO");
			}
			break;
		}
	}

	/**
	 * Returns a convenient String in format mm:ss to be displayed 
	 * on the timer
	 * @param milliseconds the current Timer value
	 * @return the formatted String
	 */
	private String getFormattedTimerValue(long milliseconds) {
		java.util.Date now = new java.util.Date(milliseconds);
		int seconds = now.getSeconds();
		int minutes = now.getMinutes();
		String time = String.format("%02d:%02d", minutes, seconds);
		return time;
	}

	/**
	 * @author bodom_lx
	 *
	 */
	public class CountDown extends CountDownTimer {
		private long currentCoundDownMilliseconds = -1;
		
		public long getCurrentCoundDownMilliseconds() {
			return currentCoundDownMilliseconds;
		}

		public void setCurrentCoundDownMilliseconds(long currentCoundDownMilliseconds) {
			this.currentCoundDownMilliseconds = currentCoundDownMilliseconds;
		}

		public CountDown(long durationMilliseconds,
				long countDownIntervalMilliseconds) {
			super(durationMilliseconds, countDownIntervalMilliseconds);
			this.currentCoundDownMilliseconds = durationMilliseconds;
		}

		@Override
		public void onFinish() {
			it.unibz.pomodroid.persistency.Activity activity = null;
			try {
				activity = it.unibz.pomodroid.persistency.Activity
						.getActivity(activityOrigin, activityOriginId, dbHelper);
			} catch (PomodroidException e) {
				e.alertUser(context);
			}
			Event event = new Event("pomodoro","finish",new Date(),activity,counter.getCurrentCoundDownMilliseconds() / 1000);
			try {
				event.save(dbHelper);
			} catch (PomodroidException e) {
				try {
					throw new PomodroidException("ERROR, could not save Event! " +e.getMessage());
				} catch (PomodroidException e1) {
					
				}
			}
			textViewPomodoroTimer.setText(getFormattedTimerValue(0));
			buttonPomodoroStart.setClickable(true);
			buttonPomodoroStop.setClickable(false);
			activity.addOnePomodoro();

			try {
				activity.save(dbHelper);
				Integer numberPomodoro = activity.getNumberPomodoro();
				textViewActivityNumberPomodoro.setText(numberPomodoro.toString());
			} catch (PomodroidException e) {
				e.alertUser(context);
			}
			
			try {
				throw new PomodroidException("Pomodoro Finished.");
			} catch (PomodroidException e) {
				e.alertUser(context, "INFO");
			}
		}

		public void stop() throws PomodroidException {
			super.cancel();
			
			textViewPomodoroTimer
					.setText(getFormattedTimerValue(pomodoroDurationMilliseconds));
			throw new PomodroidException("Pomodoro Broken");
		}

		@Override
		public void onTick(long millisUntilFinished) {
			this.currentCoundDownMilliseconds = millisUntilFinished;
			textViewPomodoroTimer
					.setText(getFormattedTimerValue(millisUntilFinished));
		}

	}
}