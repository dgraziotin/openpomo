package it.unibz.pomodroid.persistency;

import com.db4o.ObjectSet;

import android.content.Context;

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
		DBHelper dbHelper = new DBHelper(this.context);
		dbHelper.getDatabase().store(this);
		dbHelper.close();
		dbHelper = null;
	}
	
	public User retrieve(){
		DBHelper dbHelper = new DBHelper(this.context);
		User prototype = new User(null,null,null,null);
		ObjectSet<User> result =  dbHelper.getDatabase().queryByExample(prototype);
		dbHelper.close();
		dbHelper = null;
		try{
			return result.get(0);
		}catch(Exception e){
			return null;
		}
	}
	

}
