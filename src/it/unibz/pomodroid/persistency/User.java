package it.unibz.pomodroid.persistency;

import com.db4o.ObjectSet;

import android.util.Log;

/**
 * @author Thomas Schievenin
 *
 * A class representing an extension of the user class. Whit the help of the open source object 
 * database db40 an user is saved into a local database.
 *
 */
public class User extends it.unibz.pomodroid.models.User {

	/**
	 * @param tracUsername
	 * @param tracPassword
	 * @param tracUrl
	 * @param promUrl
	 */
	public User(String tracUsername, String tracPassword, String tracUrl, String promUrl) {
		super(tracUsername, tracPassword, tracUrl, promUrl);
	}

	
	/**
	 * Save an user
	 * 
	 * @param dbHelper
	 */
	public void save(DBHelper dbHelper){
		try{
			dbHelper.getDatabase().store(this);
			Log.i("User.save()", "User Saved.");
		}catch(Exception e){
			Log.e("User.save()", "Problem: " + e.getMessage());
		}
	}
	
	/**
	 * Returns the first user in the db
	 * 
	 * @param dbHelper
	 * @return an object of type user
	 */
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
