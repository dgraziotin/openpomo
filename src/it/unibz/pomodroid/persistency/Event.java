package it.unibz.pomodroid.persistency;


import java.util.Date;
import java.util.List;

import android.util.Log;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;

public class Event extends it.unibz.pomodroid.models.Event{

	public Event(String type, String value, Date timestamp,
			Activity activity) {
		super(type, value, timestamp, activity);
	}

	
	public void save(DBHelper dbHelper){
		try{
			dbHelper.getDatabase().store(this);
		}catch(Exception e){
			Log.e("Event.save(single)", "Problem: " + e.getMessage());
		}
	}
	
	public static void deleteAll(DBHelper dbHelper){
		ObjectSet<Event> retrievedEvents;
		try{
			retrievedEvents = dbHelper.getDatabase().queryByExample(Event.class);
			dbHelper.getDatabase().delete(retrievedEvents);
		}catch(Exception e){
			Log.e("Event.deleteAll()", "Problem: " + e.getMessage());
		}
	}

	public static List<Event> getAll(DBHelper dbHelper){
		ObjectSet<Event> result;
		try{
			result = dbHelper.getDatabase().queryByExample(Event.class);
			return result;
		}catch(Exception e){
			Log.e("Event.getAll()", "Problem: " + e.getMessage());
			return null;
		}
	}
	
	public static List<Event> getAll(final Activity activity, DBHelper dbHelper){
		ObjectSet<Event> result;
		try{
			result = dbHelper.getDatabase().query(
					new Predicate<Event>() {
						private static final long serialVersionUID = 1L;

						public boolean match(Event event) {
							return event.getActivity().equals(activity);
						}
					});
		}catch(Exception e){
			Log.e("Event.getAll()", "Problem: " + e.getMessage());
			return null;
		}
		return result;
	}


	public void delete(DBHelper dbHelper) {
		ObjectSet<Event> result;
		try{
		result = dbHelper.getDatabase()
				.queryByExample(this);
		Event found = (Event) result.next();
		dbHelper.getDatabase().delete(found);
		}catch(Exception e){
			Log.e("Event.delete()", "Problem: " + e.getMessage());
		}
		
	}

}
