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


public abstract class SharedActivity extends Activity {
	
	protected DBHelper dbHelper;
	protected User user;
	protected Context context;
	
	public DBHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(DBHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	
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
	
	@Override
	public void onPause() {
		super.onPause();
		this.dbHelper.commit();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		this.dbHelper.close();
	}
	
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        SharedMenu.onCreateOptionsMenu(menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return SharedMenu.onOptionsItemSelected(item);
	}
}
