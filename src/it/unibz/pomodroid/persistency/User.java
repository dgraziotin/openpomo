package it.unibz.pomodroid.persistency;


import com.db4o.ObjectSet;

import android.util.Log;

public class User extends it.unibz.pomodroid.models.User {

	public User(String tracUsername, String tracPassword, String tracUrl) {
		super(tracUsername, tracPassword, tracUrl);
	}

	
	public void save(DBHelper dbHelper){
		try{
			dbHelper.getDatabase().store(this);
		}catch(Exception e){
			Log.e("User.save()", "Problem: " + e.getMessage());
		}
	}
	
	public static User retrieve(DBHelper dbHelper){
		ObjectSet<User> users;
		try{
			users = dbHelper.getDatabase().queryByExample(User.class);
			return users.next();
		}catch(Exception e){
			Log.e("User.retrieve()", "Problem: " + e.getMessage());
			return null;
		}
	
	}
}
