package it.unibz.pomodroid.persistency;

import java.security.Timestamp;
import java.util.List;

import android.content.Context;

import com.db4o.ObjectSet;

import it.unibz.pomodroid.models.Activity;
import it.unibz.pomodroid.persistency.DBHelper;

public class Event extends it.unibz.pomodroid.models.Event{
	
	private Activity activity;
	private Context context;
	
	public Event(String type, Timestamp datetime, Activity activity, Context context) {
		super(type, datetime, activity);
		this.context = context;
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
	
	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void save(){
		DBHelper dbHelper = new DBHelper(this.context);
		dbHelper.getDatabase().store(this);
	}
	
	public void deleteAll(){
		DBHelper dbHelper = new DBHelper(this.context);
		Event prototype = new Event(null,null,null,null);
		ObjectSet<Event> retrievedEvents = dbHelper.getDatabase().queryByExample(prototype);
		dbHelper.getDatabase().delete(retrievedEvents);
		dbHelper.close();
		dbHelper = null;
	}

	public List<Event> getAll(){
		DBHelper dbHelper = new DBHelper(this.context);
		Event prototype = new Event(null,null,null,null);
		ObjectSet<Event> result = dbHelper.getDatabase().queryByExample(prototype);
		dbHelper.close();
		dbHelper = null;
		return result;
	}

}
