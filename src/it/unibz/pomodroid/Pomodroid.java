package it.unibz.pomodroid;

import it.unibz.pomodroid.persistency.DBHelper;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Pomodroid extends Activity {
	private DBHelper dbHelper;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = new DBHelper(this);
		TextView tv = new TextView(this);
		tv.setText("Hello Pomodroid");
		setContentView(tv);
		// setContentView(R.layout.main);
		this.dbHelper.close();
	}

}