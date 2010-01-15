package it.unibz.pomodroid;

import java.util.Date;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.User;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class implements graphically the pomodoro technique. Here we have
 * the counter and the description of the activity to face.
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedActivity
 * @see android.view.View.OnClickListener;
 */

public class Pomodoro extends SharedActivity implements OnClickListener {
	public final static int SUCCESS_RETURN_CODE = 1;
	private final static int SECONDS_PER_MINUTES = 60;
	private final static int MILLISECONDS_PER_SECONDS = 1000;
	private int pomodoroDurationMilliseconds;
	private TextView textViewPomodoroTimer = null;
	private TextView textViewActivitySummary = null;
	private TextView textViewActivityDescription = null;
	private TextView textViewActivityNumberPomodoro = null;
	private Button buttonPomodoroStart = null;
	private Button buttonPomodoroStop = null;
	private CountDown counter = null;
	private String activityOrigin = null;
	private int activityOriginId = -1;

	/** Called when the activity is first created. */

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibz.pomodroid.SharedActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pomodoro);
		this.activityOrigin = this.getIntent().getExtras().getString("origin");
		this.activityOriginId = this.getIntent().getExtras().getInt("originId");
		it.unibz.pomodroid.persistency.Activity activity = null;
		try {
			activity = it.unibz.pomodroid.persistency.Activity.getActivity(
					this.activityOrigin, this.activityOriginId, super.dbHelper);
			this.user = User.retrieve(super.dbHelper);
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
		this.textViewActivityDescription = (TextView) findViewById(R.id.TextViewActivityDescription);
		this.textViewActivityNumberPomodoro = (TextView) findViewById(R.id.TextViewActivityNumberPomodoro);
		this.textViewActivitySummary.setText(activity.getSummary() + " ("
				+ activity.getStringDeadline() + ")");
		this.textViewActivityDescription.setText(activity.getDescription()
				+ " (Given by: " + activity.getReporter() + ")");
		Integer numberPomodoro = activity.getNumberPomodoro();
		this.textViewActivityNumberPomodoro.setText("Number of pomodoro: "
				+ numberPomodoro.toString());

		this.pomodoroDurationMilliseconds = user.getPomodoroMinutesDuration()
				* SECONDS_PER_MINUTES * MILLISECONDS_PER_SECONDS;

		/*
		 * If you want to set the pomodoro duration equals to 10 seconds remove
		 * the following comment symbol
		 */

		// this.pomodoroDurationMilliseconds = 10000; // FIXME: delete it before
		// production!

		counter = new CountDown(this.pomodoroDurationMilliseconds,
				MILLISECONDS_PER_SECONDS);
		this.textViewPomodoroTimer.setText(this
				.getFormattedTimerValue(this.pomodoroDurationMilliseconds));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		it.unibz.pomodroid.persistency.Activity activity = null;
		try {
			activity = it.unibz.pomodroid.persistency.Activity.getActivity(
					this.activityOrigin, this.activityOriginId, super.dbHelper);
			switch (v.getId()) {
			case R.id.ButtonPomodoroStart:
				Event event = new Event("pomodoro", "start", new Date(),
						activity, pomodoroDurationMilliseconds / 1000);

				event.save(super.dbHelper);
				counter.start();
				this.buttonPomodoroStart.setClickable(false);
				this.buttonPomodoroStop.setClickable(true);
				break;
			case R.id.ButtonPomodoroStop:

				Event event1 = new Event("pomodoro", "stop", new Date(),
						activity,
						counter.getCurrentCoundDownMilliseconds() / 1000);
				event1.save(super.dbHelper);
				this.buttonPomodoroStart.setClickable(true);
				this.buttonPomodoroStop.setClickable(false);
				counter.stop();
				break;
			}
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}

	/**
	 * Returns a convenient String in format mm:ss to be displayed on the timer
	 * 
	 * @param milliseconds
	 *            the current Timer value
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
	 * This class implements the pomodoro countdown
	 * 
	 */
	public class CountDown extends CountDownTimer {
		private long currentCoundDownMilliseconds = -1;

		/**
		 * @return current countdown in milliseconds
		 */
		public long getCurrentCoundDownMilliseconds() {
			return currentCoundDownMilliseconds;
		}

		/**
		 * @param currentCoundDownMilliseconds
		 */
		public void setCurrentCoundDownMilliseconds(
				long currentCoundDownMilliseconds) {
			this.currentCoundDownMilliseconds = currentCoundDownMilliseconds;
		}

		/**
		 * @param durationMilliseconds
		 * @param countDownIntervalMilliseconds
		 */
		public CountDown(long durationMilliseconds,
				long countDownIntervalMilliseconds) {
			super(durationMilliseconds, countDownIntervalMilliseconds);
			this.currentCoundDownMilliseconds = durationMilliseconds;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.CountDownTimer#onFinish()
		 */
		@Override
		public void onFinish() {
			it.unibz.pomodroid.persistency.Activity activity = null;
			try {
				activity = it.unibz.pomodroid.persistency.Activity.getActivity(
						activityOrigin, activityOriginId, dbHelper);

				Event event = new Event("pomodoro", "finish", new Date(),
						activity,
						counter.getCurrentCoundDownMilliseconds() / 1000);
				event.save(dbHelper);

				textViewPomodoroTimer.setText(getFormattedTimerValue(0));
				buttonPomodoroStart.setClickable(true);
				buttonPomodoroStop.setClickable(false);
				activity.addOnePomodoro();
				activity.save(dbHelper);
				Integer numberPomodoro = activity.getNumberPomodoro();
				textViewActivityNumberPomodoro.setText("Number of pomodoro: "
						+ numberPomodoro.toString());
				if (user.isLongerBreak(dbHelper))
					throw new PomodroidException(context
							.getString(R.string.pomodoro_short_break));
				else
					throw new PomodroidException(context
							.getString(R.string.pomodoro_long_break));

			} catch (PomodroidException e) {
				e.alertUser(context);
			} finally {
				dbHelper.commit();
			}
		}

		/**
		 * Stops the counter
		 * 
		 * @throws PomodroidException
		 * 
		 */
		public void stop() throws PomodroidException {
			super.cancel();
			textViewPomodoroTimer
					.setText(getFormattedTimerValue(pomodoroDurationMilliseconds));
			throw new PomodroidException(context
					.getString(R.string.pomodoro_broken));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.CountDownTimer#onTick(long)
		 */
		@Override
		public void onTick(long millisUntilFinished) {
			this.currentCoundDownMilliseconds = millisUntilFinished;
			textViewPomodoroTimer
					.setText(getFormattedTimerValue(millisUntilFinished));
		}

	}
}