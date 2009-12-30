package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
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
	private it.unibz.pomodroid.persistency.Activity activity = null;
	private User user = null;
	private Context context = null;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pomodoro);
		this.dbHelper = new DBHelper(this);
		this.context = this;
		String origin = this.getIntent().getExtras().getString("origin");
		int originId = this.getIntent().getExtras().getInt("originId");
		try {
			this.activity = it.unibz.pomodroid.persistency.Activity
					.getActivity(origin, originId, this.dbHelper);
			this.user = User.retrieve(dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
		this.textViewPomodoroTimer = (TextView) findViewById(R.id.TextViewPomodoroTimer);
		this.buttonPomodoroStart = (Button) findViewById(R.id.ButtonPomodoroStart);
		this.buttonPomodoroStart.setOnClickListener((OnClickListener) this);
		this.buttonPomodoroStop = (Button) findViewById(R.id.ButtonPomodoroStop);
		this.buttonPomodoroStop.setOnClickListener((OnClickListener) this);

		this.textViewActivitySummary = (TextView) findViewById(R.id.TextViewActivitySummary);
		this.textViewActivityDeadline = (TextView) findViewById(R.id.TextViewActivityDeadline);
		this.textViewActivityDescription = (TextView) findViewById(R.id.TextViewActivityDescription);
		this.textViewActivityNumberPomodoro = (TextView) findViewById(R.id.TextViewActivityNumberPomodoro);
		this.textViewActivityReporter = (TextView) findViewById(R.id.TextViewActivityReporter);

		this.textViewActivitySummary.setText(this.activity.getSummary());
		this.textViewActivityDeadline.setText(this.activity.getDeadline()
				.toString());
		this.textViewActivityDescription
				.setText(this.activity.getDescription());
		Integer numberPomodoro = this.activity.getNumberPomodoro();
		this.textViewActivityNumberPomodoro.setText(numberPomodoro.toString());
		this.textViewActivityReporter.setText(this.activity.getReporter());

		this.pomodoroDurationMilliseconds = user.getPomodoroMinutesDuration()
				* SECONDS_PER_MINUTES * MILLISECONDS_PER_SECONDS;
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
		Intent intent = new Intent();
		setResult(SUCCESS_RETURN_CODE, intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ButtonPomodoroStart:
			counter.start();
			this.buttonPomodoroStart.setClickable(false);
			break;
		case R.id.ButtonPomodoroStop:
			this.buttonPomodoroStart.setClickable(true);
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
		public CountDown(long durationMilliseconds,
				long countDownIntervalMilliseconds) {
			super(durationMilliseconds, countDownIntervalMilliseconds);
		}

		@Override
		public void onFinish() {
			textViewPomodoroTimer.setText(getFormattedTimerValue(0));
			buttonPomodoroStart.setClickable(true);
			activity.setNumberPomodoro(activity.getNumberPomodoro() + 1);

			try {
				activity.save(dbHelper);
				Integer numberPomodoro = activity.getNumberPomodoro();
				textViewActivityNumberPomodoro.setText(numberPomodoro
						.toString());
				activity = it.unibz.pomodroid.persistency.Activity.getActivity(activity.getOrigin(), activity.getOriginId(), dbHelper);
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
			textViewPomodoroTimer
					.setText(getFormattedTimerValue(millisUntilFinished));
		}

	}
}