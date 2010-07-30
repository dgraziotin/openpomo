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
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity Inventory Sheet class is an extension of Shared List activity.
 * This class shows to the user which activities are not faced yet, but have 
 * to be done.
 * The only way to erase an activity from this sheet is to put it into the trash.
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * 
 * @see it.unibz.pomodroid.SharedListActivity
 */
public class ActivityInventorySheet extends SharedListActivity {

	static final int ACTION_ADD = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setResourceLayout(R.layout.aisactivityentry);
		super.setContext(this);
		super.onCreate(savedInstanceState);
	}

	/**
	 * Gets the Activities from Activity.getUncompleted() and adds them to the local
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
			List<Activity> retrievedActivities = Activity.getUncompleted(this.dbHelper);
			activities.addAll(retrievedActivities);
		} catch (Exception e) {
			throw new PomodroidException("Error in retrieving Activities from the DB!");
		}
		this.runOnUiThread(populateAdapter);
	}
	
	/**
	 * We specify the menu labels and theirs icons
	 * @param menu
	 * @return true 
	 *
	 */
	@Override
	public  boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ACTION_ADD, 0, "Add a new Activity").setIcon(
				android.R.drawable.ic_menu_add);
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
		Intent i; 
		switch (item.getItemId()) {
		case ACTION_ADD:
			i = new Intent(this, EditActivity.class);
			this.startActivity(i);
			return true;
		}
		return false;
	}

	/**
	 * This dialog gives the possibility to change the status of each activity
	 * 
	 * @param activity
	 */
	@Override
	protected void openActivityDialog(Activity activity) {
		final Activity selectedActivity = activity;
		int aisDialog = -1;
		if (selectedActivity.getOrigin().equals("local"))
			aisDialog = R.array.ais_dialog_local;
		else
			aisDialog = R.array.ais_dialog;
		
		new AlertDialog.Builder(this).setTitle(R.string.activity_title)
				.setItems(aisDialog,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								try {
									switch (i) {
									case 0:
										selectedActivity.setTodoToday(true);
										selectedActivity.setUndone();
										selectedActivity.save(dbHelper);
										activityAdapter.notifyDataSetChanged();
										break;
									case 1:
										selectedActivity.close(dbHelper);
										activityAdapter.remove(selectedActivity);
										break;
									case 2:
										Intent intent = new Intent();
										intent.setClass(getContext(), EditActivity.class);
										Bundle bundle = new Bundle();
										bundle.putInt("originId", selectedActivity.getOriginId());
										intent.putExtras(bundle);
										startActivity(intent);
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

}