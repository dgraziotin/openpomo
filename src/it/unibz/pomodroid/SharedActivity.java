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
 *   along with Pomodroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibz.pomodroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;

/**
 * Base-class of all activities. Defines common behavior for all Activities of
 * Pomodroid.
 * 
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see android.app.Activity
 */
public abstract class SharedActivity extends Activity {
	/**
	 * Represents the intention of adding a new Activity
	 */
	public static final int ACTION_ADD_ACTIVITY = 1;
	/**
	 * Represents the intention of adding a new Service
	 */
	public static final int ACTION_ADD_SERVICE = 2;
	/**
	 * Represents the intention of going to the About window
	 */
	public static final int ACTION_GO_ABOUT = 3;
	/**
	 * Represents the intention of going to the AIS window
	 */
	public static final int ACTION_GO_AIS = 4;
	/**
	 * Represents the intention of going to the Preferences window
	 */
	public static final int ACTION_GO_PREFERENCES = 5;
	/**
	 * Represents the intention of going to the Services window
	 */
	public static final int ACTION_GO_SERVICES = 6;
	/**
	 * Represents the intention of going to the TS window
	 */
	public static final int ACTION_GO_TS = 7;
	/**
	 * Represents the intention of going to the TTS window
	 */
	public static final int ACTION_GO_TTS = 8;
	/**
	 * Represents the intention of going to the List Services window
	 */
	public static final int ACTION_LIST_SERVICES = 9;
	/**
	 * Represents the intention of save
	 */
	public static final int ACTION_SAVE = 10;
	/**
	 * Represents the intention of starting a pomodoro
	 */
	public static final int ACTION_POMODORO_START = 11;
	/**
	 * Represents the intention of stopping a pomodoro
	 */
	public static final int ACTION_POMODORO_STOP = 12;
	


	/**
	 * The Database container for db4o
	 */
	private DBHelper dbHelper;
	/**
	 * The current user
	 */
	private User user;
	/**
	 * The Context of the Activity
	 */
	protected Context context;

	/**
	 * @return dbHelper object
	 */
	public DBHelper getDbHelper() {
		return dbHelper;
	}

	/**
	 * Set dbHelper
	 * 
	 * @param dbHelper
	 * 
	 */
	public void setDbHelper(DBHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	/**
	 * @return user object
	 */
	public User getUser() {
		if (user == null) {
			try {
				if (User.isPresent(dbHelper)) {
					this.setUser(User.retrieve(dbHelper));
				} else {
					this.user = new User();
					this.user.setPomodoroMinutesDuration(25);
					this.user.save(this.dbHelper);
				}
			} catch (PomodroidException e) {
				e.alertUser(this);
			}
		}
		return user;
	}


	/**
	 * User to set
	 * 
	 * @param user
	 * 
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Every sub-class of this one automatically receive an instance of the User
	 * and of the Database Container. This method is also responsible of
	 * bringing the User to the Preferences the first time it starts Pomodroid
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.dbHelper = new DBHelper(getApplicationContext());
		this.context = this;
		this.user = this.getUser();
	}


	/**
	 * Every time an Activity looses focus, it is forced to commit changes to
	 * the Database
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		this.dbHelper.commit();
	}

	/**
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * @see android.app.Activity#onResume()
	 */
	public void onResume() {
		super.onResume();
	}

	
	/**
	 * Wrapper for starting Android Activities and adding
	 * Intents and Flags
	 * @param klass - the class to be started
	 * @param finishCurrentActivity - true if calling activity must terminate
	 * @param recycleActivity - true if the called activity can be retrieved from stack if present
	 */
	public void startActivity(Class<?> klass, boolean finishCurrentActivity,
			boolean recycleActivity) {
		Intent intent = new Intent(this.context, klass);
		if (recycleActivity)
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		if (finishCurrentActivity)
			finish();
	}

	/**
	 * Checks if a string is null or empty
	 * 
	 * @param string
	 *            the string to be checked
	 * @return true if the string is not null or not empty
	 */
	public boolean nullOrEmpty(String string) {
		return string.equals("") || string == null;
	}
}
