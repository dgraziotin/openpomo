package it.unibz.pomodroid.persistency;

import java.security.Timestamp;
import java.util.List;

import android.content.Context;

import com.db4o.ObjectSet;

import it.unibz.pomodroid.models.Activity;
import it.unibz.pomodroid.persistency.DBHelper;

public class Event extends it.unibz.pomodroid.models.Event{
	
	private Activity activity;
	
	public Event(String type, String value, Timestamp timestamp,
			Activity activity) {
		super(type, value, timestamp, activity);
		this.activity = activity;
	}

	/**
	 * @return the activity
	 */
	@Override
	public Activity getActivity() {
		return activity;
	}

	/**
	 * @param activity the activity to set
	 */
	@Override
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	
	public void save(Context context){
		DBHelper dbHelper = new DBHelper(context);
		dbHelper.getDatabase().store(this);
		dbHelper.getDatabase().close();
		dbHelper = null;
	}
	
	public void deleteAll(Context context){
		DBHelper dbHelper = new DBHelper(context);
		ObjectSet<Event> retrievedEvents = dbHelper.getDatabase().queryByExample(Event.class);
		dbHelper.getDatabase().delete(retrievedEvents);
		dbHelper.close();
		dbHelper = null;
	}

	public List<Event> getAll(Context context){
		DBHelper dbHelper = new DBHelper(context);
		ObjectSet<Event> result = dbHelper.getDatabase().queryByExample(Event.class);
		dbHelper.close();
		dbHelper = null;
		return result;
	}

}
