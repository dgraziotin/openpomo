package it.unibz.pomodroid.persistency;

import java.sql.Timestamp;
import java.util.List;
import com.db4o.ObjectSet;
import android.content.Context;
import it.unibz.pomodroid.persistency.DBHelper;

public class Activity extends it.unibz.pomodroid.models.Activity {

	private Context context;
	
	public Activity(int numberPomodoro, Timestamp received, Timestamp deadline,
			String description, String origin, int originId, String reporter,
			String type, Context context) {
		super(numberPomodoro, received, deadline, description, origin,
				originId, reporter, type);
		
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	
	public void save() {
		DBHelper dbHelper = new DBHelper(this.context);
		dbHelper.getDatabase().store(this);
	    dbHelper.getDatabase().close();
	    dbHelper = null;
	}

	public void save(List<Activity> Activities) {
		DBHelper dbHelper = new DBHelper(this.context);
		for (Activity activity : Activities)
		{
			dbHelper.getDatabase().store(activity);
		    dbHelper.getDatabase().close();
		}
		dbHelper = null;
	}

	@SuppressWarnings("unchecked")
	public void delete() {
		DBHelper dbHelper = new DBHelper(this.context);
		ObjectSet result=dbHelper.getDatabase().queryByExample(this);
		Activity found=(Activity)result.next(); 
		dbHelper.getDatabase().delete(found);
		dbHelper.getDatabase().close();
	    dbHelper = null;
	}
}
