package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Event;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CleanDatabase extends SharedActivity implements OnClickListener {

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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ButtonDeleteActivitiesEvents:
			try {
				it.unibz.pomodroid.persistency.Activity.deleteAll(dbHelper);
				Event.deleteAll(dbHelper);
				throw new PomodroidException(
						"All activities and events deleted!", "INFO");
			} catch (PomodroidException e) {
				e.alertUser(context);
			} finally {
				dbHelper.close();
			}
			break;
		case R.id.ButtonDefragmentDatabase:
			try {
				dbHelper.defragment();
				throw new PomodroidException(
						"Database defragmented.", "INFO");
			} catch (PomodroidException e) {
				e.alertUser(context);
			} finally {
				dbHelper.close();
			}
		break;
	case R.id.ButtonDeleteDatabase:
		try {
			dbHelper.deleteDatabase();
			throw new PomodroidException(
					"Database destroyed. Please restart Pomodroid.", "INFO");
		} catch (PomodroidException e) {
			e.alertUser(context);
		} finally {
			dbHelper.close();
		}
	}
	}

}
