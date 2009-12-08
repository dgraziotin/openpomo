package it.unibz.pomodroid;

import java.util.Date;
import java.sql.Timestamp;

import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class Pomodroid extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setText("Start");

		User user = User.retrieve(this);
		
		Date now = new Date();
		
		/*
		Timestamp timestamp = new Timestamp(now.getTime());
		it.unibz.pomodroid.persistency.Activity ac = new it.unibz.pomodroid.persistency.Activity(
				0, timestamp, timestamp, "A dummy activity", "TRAC", 666,
				"tschievenin", "bug", this);
		ac.save();
		*/
	
		//Boolean ac = it.unibz.pomodroid.persistency.Activity.retrieve(this);
		
	
		//tv.setText(user);
		//tv.setText(ac.toString());
		setContentView(tv);
		// setContentView(R.layout.main);
	}
}