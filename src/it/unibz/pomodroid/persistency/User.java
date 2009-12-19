package it.unibz.pomodroid.persistency;

import com.db4o.ObjectSet;

import android.util.Log;

/**
 * @author bodom_lx
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

	
	public void save(){
		try{
			DBHelper.getDatabase().store(this);
		}catch(Exception e){
			Log.e("User.save()", "Problem: " + e.getMessage());
		}
	}
	
	public static User retrieve(){
		ObjectSet<User> users;
		try{
			users = DBHelper.getDatabase().queryByExample(User.class);
			return users.next();
		}catch(Exception e){
			Log.e("User.retrieve()", "Problem: " + e.getMessage());
			return null;
		}
	
	}
}
