package it.unibz.pomodroid.persistency;

import java.util.List;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import android.content.Context;
import android.util.Log;
public class User extends it.unibz.pomodroid.models.User {

	private Context context;
	
	public User(String tracUsername, String tracPassword, String tracUrl, Context context) {
		super(tracUsername, tracPassword, tracUrl);
		this.context = context;
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
		DBHelper dbHelper = null;
		try{
			dbHelper = new DBHelper(this.context);
			this.context = null;
			dbHelper.getDatabase().store(this);
		}catch(Exception e){
			Log.e("User", "Problem: " + e.getMessage());
		}finally{
			dbHelper.close();
			dbHelper = null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public User retrieve(){
		DBHelper dbHelper = new DBHelper(this.context);
		List<User> users = dbHelper.getDatabase().query().execute();
		return users.get(0);
	}
}
