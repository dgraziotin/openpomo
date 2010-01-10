package it.unibz.pomodroid.persistency;

import java.util.Date;
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
			Log.e("User.isPresent()", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in User.isPresent():"+e.toString());
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
				Log.e("User.save()", "Problem: " + e.toString());
				throw new PomodroidException("ERROR in User.save()" + e.toString());
			}
		} else {
			try{
				User updateUser = retrieve(dbHelper);
				updateUser.update(this);
				dbHelper.getDatabase().store(updateUser);
			}catch(Exception e){
				Log.e("User.save()", "Update Problem: " + e.toString());
				throw new PomodroidException("ERROR in User.save(update)" + e.toString());
			}finally{
				dbHelper.close();
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
			if (users==null || users.isEmpty())
				return null;
			else
				return users.next();
		}catch(Exception e){
			Log.e("User.retrieve()", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in User.retrieve()" + e.toString());
		}
	
	}
	
	public boolean isSameDay(Date userDate){
		Date today = new Date();
		// FIXME: use method after or before instead of 3 different methods
		// today.setHours(23);
		// today.setMinutes(59);
		// today.setSeconds(59);
		// return today.after(user.getDateFacedPomodoro());
		return (today.getDay() == userDate.getDay() && 
				today.getMonth() == userDate.getMonth() &&
				today.getYear() == userDate.getYear());
	}
	
	public boolean isLongerBreak(DBHelper dbHelper) throws PomodroidException{
		User user = User.retrieve(dbHelper);
		if (isSameDay(user.getDateFacedPomodoro())) {
			user.addPomodoro();
			user.save(dbHelper);
			return user.isFourthPomodoro();
		} else {
			user.setFacedPomodoro(1);
			user.setDateFacedPomodoro(new Date());
			user.save(dbHelper);
			return false;
		}
	}

}
