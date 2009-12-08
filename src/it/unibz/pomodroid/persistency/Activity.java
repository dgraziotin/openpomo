package it.unibz.pomodroid.persistency;

import java.sql.Timestamp;
import java.util.List;
import com.db4o.ObjectSet;


import android.content.Context;
import android.util.Log;
import it.unibz.pomodroid.persistency.DBHelper;

/**
 * @author bodom_lx
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
	public Activity(int numberPomodoro, Timestamp received, Timestamp deadline,
			String description, String origin, int originId, String reporter,
			String type) {
		super(numberPomodoro, received, deadline, description, origin,
				originId, reporter, type);
		// TODO Auto-generated constructor stub
	}

	
	public void save(Context context){
		DBHelper dbHelper = null;
		try{
			dbHelper = new DBHelper(context);
			dbHelper.getDatabase().store(this);
		}catch(Exception e){
			Log.e("Activity", "Problem: " + e.getMessage());
		}finally{
			dbHelper.close();
			dbHelper = null;
		}
	}

	public void save(List<Activity> Activities, Context context) {
		DBHelper dbHelper = new DBHelper(context);
		for (Activity activity : Activities)
		{
			dbHelper.getDatabase().store(activity);
		    dbHelper.getDatabase().close();
		}
		dbHelper = null;
	}


	public void delete(Context context) {
		DBHelper dbHelper = new DBHelper(context);
		ObjectSet<Activity> result=dbHelper.getDatabase().queryByExample(this);
		Activity found=(Activity)result.next(); 
		dbHelper.getDatabase().delete(found);
		dbHelper.getDatabase().close();
	    dbHelper = null;
	}
	

	public static Activity retrieve(Context context){
		DBHelper dbHelper = new DBHelper(context);
		ObjectSet<Activity> result = null;
		try{
		 result = dbHelper.getDatabase().queryByExample(Activity.class);
		}catch(Exception e){
			Log.e("Activity.retrieve", e.toString());
		}
		if (result.isEmpty())
			return null;
		else
			return result.next();
	}

}
