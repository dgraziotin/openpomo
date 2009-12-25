package it.unibz.pomodroid.persistency;

import java.util.Date;
import java.util.List;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import android.util.Log;
import it.unibz.pomodroid.persistency.DBHelper;

/**
 * @author Thomas Schievenin
 * 
 * A class representing an extension of the activity class. Whit the help of the open source object 
 * database db40 the activity is saved into a local database.
 *
 */
public class Activity extends it.unibz.pomodroid.models.Activity {

	/**
	 * @param numberPomodoro
	 * @param received
	 * @param deadline
	 * @param description
	 * @param origin
	 * @param originId
	 * @param reporter
	 * @param type
	 * @param context
	 */
	public Activity(int numberPomodoro, Date received, Date deadline,
			String summary, String description, String origin, int originId, String priority, String reporter,
			String type) {
		super(numberPomodoro, received, deadline, summary, description, origin,
				originId, priority, reporter, type);
		// TODO Auto-generated constructor stub
	}

	
	
	/**
	 * 
	 * @param origin
	 * @param originId
	 * @param dbHelper
	 * @return a specific activity
	 */
	public static boolean isPresent(final String origin, final int originId,
			DBHelper dbHelper) {
		List<Activity> activities;
		try{
			activities = dbHelper.getDatabase().query(
				new Predicate<Activity>() {
					private static final long serialVersionUID = 1L;

					public boolean match(Activity activity) {
						return activity.getOrigin().equals(origin)
								&& activity.getOriginId() == originId;
					}
				});
			if (activities.isEmpty())
				return false;
			else
				return true;
		}catch(Exception e){
			Log.e("Activity.isPresent()", "Problem: " + e.getMessage());
			return false;
		}
	}

	/**
	 * @param dbHelper
	 * @return true if an activity is saved into the DB
	 */
	public boolean save(DBHelper dbHelper){
		if (!isPresent(this.getOrigin(),this.getOriginId(),dbHelper)){
			try{
				dbHelper.getDatabase().store(this);
				return true;
			}catch(Exception e){
				Log.e("Activity.save(single)", "Problem: " + e.getMessage());
				return false;
			}
		}
		return false;
	}

	/**
	 * Saves a list of activities
	 * 
	 * @param Activities
	 * @param dbHelper
	 */
	public void save(List<Activity> Activities, DBHelper dbHelper) {
		try{
			for (Activity activity : Activities)
				activity.save(dbHelper);
		}catch(Exception e){
			Log.e("Activity.save(List)", "Problem: " + e.getMessage());
		}
	}

	
	
	/**
	 * Deletes all activities
	 * 
	 * @param dbHelper
	 */
	public void delete(DBHelper dbHelper) {
		ObjectSet<Activity> result;
		try{
		result = dbHelper.getDatabase()
				.queryByExample(this);
		Activity found = (Activity) result.next();
		dbHelper.getDatabase().delete(found);
		}catch(Exception e){
			Log.e("Activity.delete()", "Problem: " + e.getMessage());
		}
	}

	
	/**
	 * Retrieves all activities
	 * 
	 * @param dbHelper
	 * @return
	 */
	public static List<Activity> getAll(DBHelper dbHelper) {
		ObjectSet<Activity> result = null;
		try{
			result = dbHelper.getDatabase().queryByExample(Activity.class);
		}catch(Exception e){
			Log.e("Activity.getAll()", "Problem: " + e.getMessage());
		}
		return result;
	}
	
	/**
	 * @param dbHelper
	 * @return the number of activities
	 */
	public static int getNumberActivities(DBHelper dbHelper){
		List<Activity> activities = Activity.getAll(dbHelper);
		return ((activities == null) ?  0 :  activities.size());
	}
	
	/**
	 * @param origin
	 * @param originId
	 * @param dbHelper
	 * @return a specific activity
	 */
	public static Activity getActivity(final String origin, final int originId,
			DBHelper dbHelper) {
		List<Activity> activities = null;
		try{
			activities = dbHelper.getDatabase().query(
				new Predicate<Activity>() {
					private static final long serialVersionUID = 1L;

					public boolean match(Activity activity) {
						return activity.getOrigin().equals(origin)
								&& activity.getOriginId() == originId;
					}
				});
		}catch(Exception e){
			Log.e("Activity.getActivity()", "Problem: " + e.getMessage());
		}
		return activities.get(0);
	}

	/**
	 * @param dbHelper
	 * @return to do today activities
	 */
	public static List<Activity> getTodoToday(DBHelper dbHelper) {
		List<Activity> activities = null;
		try{
			activities = dbHelper.getDatabase().query(
				new Predicate<Activity>() {
					private static final long serialVersionUID = 1L;

					public boolean match(Activity activity) {
						return activity.isTodoToday();
					}
				});
		}catch(Exception e){
			Log.e("Activity.getTodoToday()", "Problem: " + e.getMessage());
		}
		return activities;
	}
	
	/**
	 * @param dbHelper
	 * @return all completed activities
	 */
	public static List<Activity> getCompleted(DBHelper dbHelper) {
		List<Activity> activities = null;
		try{
			activities = dbHelper.getDatabase().query(
				new Predicate<Activity>() {
					private static final long serialVersionUID = 1L;

					public boolean match(Activity activity) {
						return activity.isDone();
					}
				});
		}catch(Exception e){
			Log.e("Activity.getCompleted()", "Problem: " + e.getMessage());
		}
		return activities;
	}
	
	/**
	 * @param dbHelper
	 * @return all uncompleted activities
	 */
	public static List<Activity> getUncompleted(DBHelper dbHelper) {
		List<Activity> activities = null;
		try{
			activities = dbHelper.getDatabase().query(
				new Predicate<Activity>() {
					private static final long serialVersionUID = 1L;

					public boolean match(Activity activity) {
						return !activity.isDone();
					}
				});
		}catch(Exception e){
			Log.e("Activity.getUncompleted()", "Problem: " + e.getMessage());
		}
		return activities;
	}
	
	/**
	 * Closes an activity
	 * 
	 * @param dbHelper
	 */
	public void close(DBHelper dbHelper) {
		try{
			this.setDone();
			this.setTodoToday(false);
			this.save(dbHelper);
		}catch(Exception e){
			Log.e("Activity.close()", "Problem: " + e.getMessage());
		}
	}
	
}
