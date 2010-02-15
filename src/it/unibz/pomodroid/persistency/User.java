/**
 * This file is part of Pomodroid.
 *
 *   Pomodroid is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Pomodroid is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibz.pomodroid.persistency;

import java.util.Date;
import java.util.List;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import it.unibz.pomodroid.exceptions.PomodroidException;

import android.util.Log;

/**
 * A class representing an extension of the user class. Whit the help of the open source object 
 * database db40 an user is saved into a local database.
 * 
 * NB: only one user will be stored into the db.
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it> 
 *
 */
public class User extends it.unibz.pomodroid.models.User {

	/**
	 * @param tracUsername
	 * @param tracPassword
	 * @param tracUrl
	 */
	public User(String tracUsername, String tracPassword, String tracUrl) {
		super(tracUsername, tracPassword, tracUrl);
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
				dbHelper.commit();
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
	
	/**
	 * Check if the date saved into the db is equal to the current one.
	 * @param userDate
	 * @return
	 * 
	 */
	public boolean isSameDay(Date userDate){
		Date today = new Date();
		return (today.getDay() == userDate.getDay() && 
				today.getMonth() == userDate.getMonth() &&
				today.getYear() == userDate.getYear());
	}
	
	/**
	 * Checks whether the user should do a short or long break
	 *  
	 * @param dbHelper
	 * @return
	 * @throws PomodroidException
	 */
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
