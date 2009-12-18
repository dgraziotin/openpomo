package it.unibz.pomodroid.persistency;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import android.util.Log;
import it.unibz.pomodroid.persistency.DBHelper;

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

	public void save(List<Activity> Activities, DBHelper dbHelper) {
		try{
			for (Activity activity : Activities)
				activity.save(dbHelper);
		}catch(Exception e){
			Log.e("Activity.save(List)", "Problem: " + e.getMessage());
		}
	}

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

	public static List<Activity> getAll(DBHelper dbHelper) {
		ObjectSet<Activity> result = null;
		try{
			result = dbHelper.getDatabase().queryByExample(Activity.class);
		}catch(Exception e){
			Log.e("Activity.getAll()", "Problem: " + e.getMessage());
		}
		return result;
	}

}
