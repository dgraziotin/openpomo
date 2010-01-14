package it.unibz.pomodroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import it.unibz.pomodroid.SharedMenu;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;


/**
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * 
 * Base-class of all activities
 *
 */
public abstract class SharedActivity extends Activity {
	
	protected DBHelper dbHelper;
	protected User user;
	protected Context context;
	
	/**
	 * @return dbHelper object
	 */
	public DBHelper getDbHelper() {
		return dbHelper;
	}

	/**
	 * @param dbHelper
	 * 
	 * Set dbHelper
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
	 * @param user
	 * 
	 * User to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedMenu.setContext(this);
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		this.dbHelper.commit();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {
		super.onStop();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	public void onResume(){
		super.onResume();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        SharedMenu.onCreateOptionsMenu(menu);
        return true;
    }
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return SharedMenu.onOptionsItemSelected(item);
	}
}
