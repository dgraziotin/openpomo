package it.unibz.pomodroid.persistency;

import java.util.Date;
import java.util.List;

import android.util.Log;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.exceptions.PomodroidException;
/**
 * @author Thomas Schievenin
 * 
 * A class representing an extension of the event class. Whit the help of the open source object 
 * database db40 the event is saved into a local database.
 *
 */
public class Event extends it.unibz.pomodroid.models.Event{

	/**
	 * @param type
	 * @param value
	 * @param timestamp
	 * @param activity
	 */
	public Event(String type, String value, Date timestamp,
			Activity activity) {
		super(type, value, timestamp, activity);
	}

	
	/**
	 * Save an event into the DB
	 * 
	 * @param dbHelper
	 * @throws PomodroidException 
	 */
	public void save(DBHelper dbHelper) throws PomodroidException{
		try{
			dbHelper.getDatabase().store(this);
		}catch(Exception e){
			Log.e("Event.save(single)", "Problem: " + e.getMessage());
			throw new PomodroidException("ERROR in Event.save()" + e.toString());
		}
	}
	
	/**
	 * Delete all events
	 * 
	 * @param dbHelper
	 * @throws PomodroidException 
	 */
	public static void deleteAll(DBHelper dbHelper) throws PomodroidException{
		ObjectSet<Event> retrievedEvents;
		try{
			retrievedEvents = dbHelper.getDatabase().queryByExample(Event.class);
			dbHelper.getDatabase().delete(retrievedEvents);
		}catch(Exception e){
			Log.e("Event.deleteAll()", "Problem: " + e.getMessage());
			throw new PomodroidException("ERROR in Event.delete()" + e.toString());
		}
	}

	/**
	 * Returns all events
	 * 
	 * @param dbHelper
	 * @return a list of events
	 * @throws PomodroidException 
	 */
	public static List<Event> getAll(DBHelper dbHelper) throws PomodroidException{
		ObjectSet<Event> result;
		try{
			result = dbHelper.getDatabase().queryByExample(Event.class);
			return result;
		}catch(Exception e){
			Log.e("Event.getAll()", "Problem: " + e.getMessage());
			throw new PomodroidException("ERROR in Event.getAll()" + e.toString());
		}
	}
	
	public static List<Event> getAll(final Activity activity, DBHelper dbHelper) throws PomodroidException{
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
			throw new PomodroidException("ERROR in Event.getAll()" + e.toString());
		}
		return result;
	}


	public void delete(DBHelper dbHelper) throws PomodroidException {
		ObjectSet<Event> result;
		try{
		result = dbHelper.getDatabase()
				.queryByExample(this);
		Event found = (Event) result.next();
		dbHelper.getDatabase().delete(found);
		}catch(Exception e){
			Log.e("Event.delete()", "Problem: " + e.getMessage());
			throw new PomodroidException("ERROR in Event.delete()" + e.toString());
		}
		
	}

}
