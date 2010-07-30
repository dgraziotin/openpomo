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

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.User;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Todo Today Sheet class is an extension of Shared List activity.
 * This class shows to the user which activities have to be done today.
 * 
 * Here we can face the activity, move it into the activity inventory sheet or
 * into the trash sheet.
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedListActivity
 */
public class TodoTodaySheet extends SharedListActivity {

	private static final int ACTION_ADD = 0;
	private static final int ACTION_PREFERENCES = 1;
	private static final int ACTION_ADVANCED_USER = 2;
	private static final int ACTION_ABOUT = 3;

	/**
	 * @see it.unibz.pomodroid.SharedListActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setResourceLayout(R.layout.ttsactivityentry);
		super.setContext(this);
		super.onCreate(savedInstanceState);
	}

	/**
	 * Gets the Activities from Activity.getTodoToday() and adds them to the local
	 * list of activities. It calls populateAdapter to populate the adapter with
	 * the new list of activities
	 * 
	 * @see it.unibz.pomodroid.persistency.Activity
	 * @throws PomodroidException
	 */
	@Override
	protected void retrieveActivities() throws PomodroidException {
		try {
			activities = new ArrayList<Activity>();
			List<Activity> retrievedActivities = Activity.getTodoToday(this.dbHelper);
			activities.addAll(retrievedActivities);
		} catch (Exception e) {
			throw new PomodroidException(
					"Error in retrieving Activities from the DB! Please try again");
		}
		this.runOnUiThread(populateAdapter);
	}

	/**
	 * This dialog gives the possibility to change the status of each activity
	 * 
	 * @param activity
	 */
	@Override
	protected void openActivityDialog(Activity activity) {
		final Activity selectedActivity = activity;
		int ttsDialog = -1;
		ttsDialog = super.user.isAdvancedUser() ? R.array.tts_dialog : R.array.tts_dialog_simple;
		
		new AlertDialog.Builder(this).setTitle(R.string.activity_title)
				.setItems(ttsDialog,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								try {
									switch (i) {
									case 0:
										Intent intent = new Intent();
										intent.setClass(
												getApplicationContext(),
												Pomodoro.class);
										Bundle bundle = new Bundle();
										bundle.putString("origin", selectedActivity
												.getOrigin());
										bundle.putInt("originId", selectedActivity
												.getOriginId());
										intent.putExtras(bundle);
										startActivity(intent);
										break;
									case 1:
										selectedActivity.close(dbHelper);
										activityAdapter
												.remove(selectedActivity);
										break;
									case 2:
										selectedActivity.setTodoToday(false);
										selectedActivity.setUndone();
										selectedActivity.save(dbHelper);
										activityAdapter
												.remove(selectedActivity);
										break;
									}
								} catch (PomodroidException e) {
									e.alertUser(getContext());
								} finally {
									dbHelper.commit();
								}
							}
						}).show();
	}
	
	/**
	 * We specify the menu labels and theirs icons
	 * @param menu
	 * @return true 
	 *
	 */
	@Override
	public  boolean onCreateOptionsMenu(Menu menu) {
		if (super.user.isAdvancedUser()){
			return true;
		}
		menu.add(0, ACTION_ADD, 0, "Add a new Activity").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(0, ACTION_PREFERENCES, 0, "Preferences").setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(0, ACTION_ABOUT, 0, "About").setIcon(
				android.R.drawable.ic_menu_help);
		return true;
	}
	
	/**
	 * As soon as the user clicks on the menu a new intent is created for adding new Activity.
	 * @param item
	 * @return
	 * 
	 */
	@Override
	public  boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case ACTION_ADD:
			intent.setClass(this, EditActivity.class);
			break;
		case ACTION_PREFERENCES:
			if(user.isAdvancedUser())
				intent.setClass(this, TabPreferences.class);
			else
				intent.setClass(this, Preferences.class);
			break;
		case ACTION_ABOUT:
			intent.setClass(this, About.class);
			break;
		 default:
			return false;
		}
		startActivity(intent);
		return true;
	}

}
