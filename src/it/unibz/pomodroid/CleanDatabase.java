package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Event;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedActivity
 * 
 *      This class allows us to manipulate the content of the object oriented
 *      database db4o.
 * 
 */

public class CleanDatabase extends SharedActivity implements OnClickListener {

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibz.pomodroid.SharedActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cleandatabase);
		Button buttonDeleteActivitiesEvents = (Button) findViewById(R.id.ButtonDeleteActivitiesEvents);
		buttonDeleteActivitiesEvents.setOnClickListener((OnClickListener) this);
		Button buttonDeleteDatabase = (Button) findViewById(R.id.ButtonDeleteDatabase);
		buttonDeleteDatabase.setOnClickListener((OnClickListener) this);
		Button buttonDefragmentDatabase = (Button) findViewById(R.id.ButtonDefragmentDatabase);
		buttonDefragmentDatabase.setOnClickListener((OnClickListener) this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * 
	 * Regarding to the button that has been clicked, the related action is
	 * called.
	 */
	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.ButtonDeleteActivitiesEvents:
				it.unibz.pomodroid.persistency.Activity.deleteAll(dbHelper);
				Event.deleteAll(dbHelper);
				throw new PomodroidException(
						"All activities and events deleted!", "INFO");
			case R.id.ButtonDefragmentDatabase:
				dbHelper.defragment();
				throw new PomodroidException("Database defragmented.", "INFO");
			case R.id.ButtonDeleteDatabase:
				dbHelper.deleteDatabase();
				throw new PomodroidException(
						"Database destroyed. Please restart Pomodroid.", "INFO");
			}
		} catch (PomodroidException e) {
			e.alertUser(context);
		} finally {
			dbHelper.commit();
		}

	}
}
