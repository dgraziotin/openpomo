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
 * Base-class of all activities. Defines common behavior for all Activities
 * of Pomodroid.
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see android.app.Activity
 */
public abstract class SharedActivity extends Activity {
	/**
	 * The Database container for db4o
	 */
	protected DBHelper dbHelper;
	/**
	 * The current user
	 */
	protected User user;
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
		return user;
	}

	/**
	 * User to set
	 * @param user
	 * 
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Every sub-class of this one automatically receive an instance
	 * of the User and of the Database Container. This method is also 
	 * responsible of bringing the User to the Preferences the first 
	 * time it starts Pomodroid
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.dbHelper = new DBHelper(getApplicationContext());
		this.context = this;
		try {
			user = User.retrieve(dbHelper);
			// protect the other activities: they can't be called until user
			// sets preferences
			if (user == null && !this.getClass().equals(Preferences.class)) {
				Intent intent = new Intent(this, Preferences.class);
				startActivity(intent);
			}
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}
	
	/**
	 * Every time an Activity looses focus, it is forced to commit changes to the 
	 * Database
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
	public void onResume(){
		super.onResume();
	}
	
}
