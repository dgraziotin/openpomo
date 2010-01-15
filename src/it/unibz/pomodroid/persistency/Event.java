package it.unibz.pomodroid.persistency;

import java.util.Date;
import java.util.List;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.exceptions.PomodroidException;

/**
 * A class representing an extension of the event class. Whit the help
 * of the open source object database db40 the event is saved into a
 * local database.
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it> * 
 * 
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

	/**
	 * @param type
	 * @param value
	 * @param timestamp
	 * @param activity
	 * @param atPomodoroValue
	 */
	public Event(String type, String value, Date timestamp, Activity activity,
			long atPomodoroValue) {
		super(type, value, timestamp, activity, atPomodoroValue);
		this.activity = activity;
	}

	/* (non-Javadoc)
	 * @see it.unibz.pomodroid.models.Event#getActivity()
	 */
	public Activity getActivity() {
		return this.activity;
	}

	/**
	 * the activity to set
	 * @param activity
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
			throw new PomodroidException("ERROR in Event.save()" + e.toString());
		}finally{
			dbHelper.commit();
		}
	}

	/**
	 * Delete all events
	 * 
	 * @param dbHelper
	 * @return
	 * @throws PomodroidException
	 */

	public static boolean deleteAll(DBHelper dbHelper)
			throws PomodroidException {
		ObjectSet<Event> events = null;
		try {
			events = dbHelper.getDatabase().query(new Predicate<Event>() {
				private static final long serialVersionUID = 1L;

				public boolean match(Event candidate) {
					return true;
				}
			});
			while (events.hasNext()) {
				dbHelper.getDatabase().delete(events.next());
			}
			return true;
		} catch (Exception e) {
			throw new PomodroidException("ERROR in Event.deleteAll():"
					+ e.toString());
		}finally{
			dbHelper.commit();
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
			throw new PomodroidException("ERROR in Event.getAll()"
					+ e.toString());
		}
	}

	/**
	 * Gets all events
	 * 
	 * @param activity
	 * @param dbHelper
	 * @return
	 * @throws PomodroidException
	 * 
	 */
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
			throw new PomodroidException("ERROR in Event.getAll()"
					+ e.toString());
		}
		return result;
	}

	

	/**
	 * Deletes all events
	 * 
	 * @param activity
	 * @param dbHelper
	 * @throws PomodroidException
	 * 
	 */
	public static void delete(final Activity activity, DBHelper dbHelper)
			throws PomodroidException {
		try {
			List<Event> eventsForActivity = Event.getAll(activity, dbHelper);
			
			for (Event ev : eventsForActivity) {
				dbHelper.getDatabase().delete(ev);
			}
		} catch (Exception e) {
			throw new PomodroidException("ERROR in Event.delete()"
					+ e.toString());
		}finally{
			dbHelper.commit();
		}
	}

}
