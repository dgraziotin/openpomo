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
import it.unibz.pomodroid.persistency.Event;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Trash Sheet class is an extension of Shared List activity.
 * This class shows to the user which activities are done.
 * 
 * From here we can move an activity into the todo today sheet (and automatically into
 * the activity inventory sheet) or into the activity inventory sheet.
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedListActivity
 */
public class TrashSheet extends SharedListActivity {

	private static final int ACTION_EMPTY = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setResourceLayout(R.layout.trashactivityentry);
		super.setContext(this);
		super.onCreate(savedInstanceState);
	}

	/**
	 * Gets the Activities from Activity.getCompleted() and adds them to the local
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
			List<Activity> retrievedActivities = Activity.getCompleted(this.dbHelper);
			activities.addAll(retrievedActivities);
		} catch (Exception e) {
			throw new PomodroidException(
					"Error in retrieving Activities from the DB!");
		}
		runOnUiThread(populateAdapter);
	}

	/**
	 * This dialog gives the possibility to change the status of each activity
	 * 
	 * @param activity
	 */
	@Override
	protected void openActivityDialog(Activity activity) {
		final Activity selectedActivity = activity;
		new AlertDialog.Builder(this).setTitle(R.string.activity_title)
				.setItems(R.array.ais_trash,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								try {
									switch (i) {
									case 0:
										selectedActivity.setTodoToday(false);
										selectedActivity.setUndone();
										selectedActivity.save(dbHelper);
										activityAdapter
												.remove(selectedActivity);
										break;
									case 1:
										selectedActivity.setTodoToday(true);
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
		menu.add(0, ACTION_EMPTY_LIST, 0, "Empty Trash Can").setIcon(
				android.R.drawable.ic_menu_delete);
		menu.add(0, ACTION_GO_AIS, 0, "Activity Inventory").setIcon(
				android.R.drawable.ic_menu_agenda);
		menu.add(0, ACTION_GO_TTS, 0, "Todo Today").setIcon(
				android.R.drawable.ic_menu_day);
		menu.add(0, ACTION_GO_PREFERENCES, 0, "Preferences").setIcon(
				android.R.drawable.ic_menu_preferences);
		return true;
	}
	
}
