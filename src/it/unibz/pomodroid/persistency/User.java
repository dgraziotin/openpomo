package it.unibz.pomodroid.persistency;

import java.util.List;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import it.unibz.pomodroid.exceptions.PomodroidException;

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
	 * @param username
	 * @param dbHelper
	 * @return
	 * @throws PomodroidException
	 */
	public static boolean isPresent(final String username, DBHelper dbHelper) throws PomodroidException {
		List<User> users;
		try{
			users = dbHelper.getDatabase().query(
				new Predicate<User>() {
					private static final long serialVersionUID = 1L;

					public boolean match(User user) {
						return user.getTracUsername().equals(username);
					}
				});
			if (users.isEmpty())
				return false;
			else
				return true;
		}catch(Exception e){
			Log.e("User.isPresent()", "Problem: " + e.getMessage());
			throw new PomodroidException("ERROR in User.isPresent():"+e.getMessage());
		}
	}
	
	/**
	 * Save an user
	 * 
	 * @param dbHelper
	 * @throws PomodroidException 
	 */
	public void save(DBHelper dbHelper) throws PomodroidException{
		if (!isPresent(this.getTracUsername(),dbHelper)){
			try{
				dbHelper.getDatabase().store(this);
				Log.i("User.save()", "User Saved.");
			}catch(Exception e){
				Log.e("User.save()", "Problem: " + e.getMessage());
				throw new PomodroidException("ERROR in User.save()" + e.toString());
			}
		} else {
			try{
				User updateUser = retrieve(dbHelper);
				updateUser = this;
				dbHelper.getDatabase().store(updateUser);
			}catch(Exception e){
				Log.e("User.save()", "Update Problem: " + e.getMessage());
				throw new PomodroidException("ERROR in User.save(update)" + e.toString());
			}
		}
	}
	
	/**
	 * Returns the first user in the db
	 * 
	 * @param dbHelper
	 * @return an object of type user
	 * @throws PomodroidException 
	 */
	public static User retrieve(DBHelper dbHelper) throws PomodroidException{
		ObjectSet<User> users;
		try{
			users = dbHelper.getDatabase().queryByExample(User.class);
			return users.next();
		}catch(Exception e){
			Log.e("User.retrieve()", "Problem: " + e.getMessage());
			throw new PomodroidException("ERROR in User.retrieve()" + e.toString());
		}
	
	}
}
