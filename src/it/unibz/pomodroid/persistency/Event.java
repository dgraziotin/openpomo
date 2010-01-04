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
 *         A class representing an extension of the event class. Whit the help
 *         of the open source object database db40 the event is saved into a
 *         local database.
 * 
 */

public class Event extends it.unibz.pomodroid.models.Event {

	Activity activity = null;

	/**
	 * @param type
	 * @param value
	 * @param timestamp
	 * @param activity
	 */
	public Event(String type, String value, Date timestamp, Activity activity) {
		super(type, value, timestamp, activity);
		this.activity = activity;
	}

	public Event(String type, String value, Date timestamp, Activity activity,
			long atPomodoroValue) {
		super(type, value, timestamp, activity, atPomodoroValue);
		this.activity = activity;
	}

	public Activity getActivity() {
		return this.activity;
	}

	/**
	 * @param activity
	 *            the activity to set
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	/**
	 * Save an event into the DB
	 * 
	 * @param dbHelper
	 * @throws PomodroidException
	 */
	public void save(DBHelper dbHelper) throws PomodroidException {
		try {
			dbHelper.getDatabase().store(this);
		} catch (Exception e) {
			Log.e("Event.save(single)", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in Event.save()" + e.toString());
		}
	}

	/**
	 * Delete all events
	 * 
	 * @param dbHelper
	 * @return
	 * @throws PomodroidException
	 */
	
	public static boolean deleteAll(DBHelper dbHelper) throws PomodroidException {
		ObjectSet<Event> events = null;
		try {			
			events = dbHelper.getDatabase().query(new Predicate<Event>() {
				private static final long serialVersionUID = 1L;
				public boolean match(Event candidate){ 
					return true;
				}
			}); 
			while(events.hasNext()) {
				dbHelper.getDatabase().delete(events.next());
			}
			return true;
		} catch (Exception e) {
			Log.e("Event.deleteAll()", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in Event.deleteAll():"+ e.toString());
		}
	}
	/**
	 * Returns all events
	 * 
	 * @param dbHelper
	 * @return a list of events
	 * @throws PomodroidException
	 */
	public static List<Event> getAll(DBHelper dbHelper)
			throws PomodroidException {
		ObjectSet<Event> result;
		try {
			result = dbHelper.getDatabase().queryByExample(Event.class);
			return result;
		} catch (Exception e) {
			Log.e("Event.getAll()", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in Event.getAll()"
					+ e.toString());
		}
	}

	public static List<Event> getAll(final Activity activity, DBHelper dbHelper)
			throws PomodroidException {
		ObjectSet<Event> result;
		try {
			result = dbHelper.getDatabase().query(new Predicate<Event>() {
				private static final long serialVersionUID = 1L;

				public boolean match(Event event) {
					return (event.getActivity().getOrigin().equals(
							activity.getOrigin()) && event.getActivity()
							.getOriginId() == activity.getOriginId());
				}
			});
		} catch (Exception e) {
			Log.e("Event.getAll()", "Problem: " + e.getMessage());
			throw new PomodroidException("ERROR in Event.getAll()"
					+ e.toString());
		}
		return result;
	}

	public static void delete(final Activity activity, DBHelper dbHelper)
			throws PomodroidException {
		try {
			List<Event> eventsForActivity = Event.getAll(activity, dbHelper);
			Log.i("Event.delete()", "Event that will be deleted: "
					+ eventsForActivity.size());
			for (Event ev : eventsForActivity) {
				dbHelper.getDatabase().delete(ev);
			}
		} catch (Exception e) {
			Log.e("Event.delete()", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in Event.delete()"
					+ e.toString());
		}
	}

}
