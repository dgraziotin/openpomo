package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Activity;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Trash Sheet class is an extension of Shared List activity.
 * This class shows to the user which activities are done.
 * 
 * From here we can move an activity into the todo today sheet (and automatically into
 * the activity inventory sheet) or into the activity inventory sheet.
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedListActivity
 */
public class TrashSheet extends SharedListActivity {

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
}
