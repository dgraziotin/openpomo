package it.unibz.pomodroid.persistency;


import com.db4o.ObjectSet;

import android.content.Context;
import android.util.Log;

public class User extends it.unibz.pomodroid.models.User {

	public User(String tracUsername, String tracPassword, String tracUrl) {
		super(tracUsername, tracPassword, tracUrl);

	}

	
	public void save(Context context){
		DBHelper dbHelper = null;
		try{
			dbHelper = new DBHelper(context);
			dbHelper.getDatabase().store(this);
		}catch(Exception e){
			Log.e("User", "Problem: " + e.getMessage());
		}finally{
			dbHelper.close();
			dbHelper = null;
		}
	}
	
	public static User retrieve(Context context){
		DBHelper dbHelper = new DBHelper(context);
		ObjectSet<User> users = dbHelper.getDatabase().queryByExample(User.class);
		return users.next();
	}
}
