package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
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

public class Pomodoro extends Activity implements OnClickListener{
	public final static int SUCCESS_RETURN_CODE = 1;
	private TextView textViewPomodoroTimer = null;
	private TextView textViewActivitySummary = null;
	private TextView textViewActivityDeadline = null;
	private TextView textViewActivityDescription = null;
	private TextView textViewActivityNumberPomodoro = null;
	private TextView textViewActivityReporter = null;
	private Button buttonPomodoroStart = null;
	private Button buttonPomodoroStop = null;
	private MyCount counter = null; 
	private DBHelper dbHelper = null;
	private it.unibz.pomodroid.persistency.Activity activity = null;
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
			this.activity = it.unibz.pomodroid.persistency.Activity.getActivity(origin, originId, this.dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
		this.textViewPomodoroTimer = (TextView) findViewById(R.id.TextViewPomodoroTimer);
		
		this.buttonPomodoroStart = (Button) findViewById(R.id.ButtonPomodoroStart);
		this.buttonPomodoroStart.setOnClickListener((OnClickListener) this);
		this.buttonPomodoroStop = (Button) findViewById(R.id.ButtonPomodoroStop);
		this.buttonPomodoroStop.setOnClickListener((OnClickListener) this);
		
		this.textViewActivitySummary = (TextView) findViewById(R.id.TextViewActivitySummary);
		this.textViewActivityDeadline   = (TextView) findViewById(R.id.TextViewActivityDeadline);
		this.textViewActivityDescription = (TextView) findViewById(R.id.TextViewActivityDescription);
		this.textViewActivityNumberPomodoro  = (TextView) findViewById(R.id.TextViewActivityNumberPomodoro);
		this.textViewActivityReporter = (TextView) findViewById(R.id.TextViewActivityReporter);
		
		this.textViewActivitySummary.setText(this.activity.getSummary());
		this.textViewActivityDeadline.setText(this.activity.getDeadline().toString());
		this.textViewActivityDescription.setText(this.activity.getDescription());
		Integer numberPomodoro = this.activity.getNumberPomodoro();
		this.textViewActivityNumberPomodoro.setText(numberPomodoro.toString());
		this.textViewActivityReporter.setText(this.activity.getReporter());
		// 5000 is the starting number (in milliseconds)
		// 1000 is the number to count down each time (in milliseconds)
		counter = new MyCount(10000, 1000);
		

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
			break;
			case R.id.ButtonPomodoroStop:
				counter.stop();
			break;
		}

	}
	
	
	// countdowntimer is an abstract class, so extend it and fill in methods
	public class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			activity.setNumberPomodoro(activity.getNumberPomodoro()+1);
			try {
				activity.save(dbHelper);
			} catch (PomodroidException e) {
				e.alertUser(context);
			}
		}

		public void stop() {
			super.cancel();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			java.util.Date now = new java.util.Date(millisUntilFinished);
			int seconds = now.getSeconds();
			int minutes = now.getMinutes();
			String time = String.format("%02d:%02d", minutes, seconds);
			textViewPomodoroTimer.setText(time);
		}
		
	}
}