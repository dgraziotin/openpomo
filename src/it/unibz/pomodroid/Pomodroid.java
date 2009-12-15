package it.unibz.pomodroid;

import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.TrackTicketFetcher;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Pomodroid extends Activity {
	private DBHelper dbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = new DBHelper(this);
		TextView tv = new TextView(this);
		
		User user = User.retrieve(dbHelper);
		TrackTicketFetcher.fetch(user, dbHelper);

		tv.setText("Fatto");
        setContentView(tv);
		// setContentView(R.layout.main);
		this.dbHelper.close();
	}

}