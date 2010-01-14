package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Activity;
import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Activity Inventory Sheet class is an extension of Shared List activity.
 * This class shows to the user which activities are not faced yet, but have 
 * to be done.
 * The only way to erase an activity from this sheet is to put it into the trash.
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * 
 * @see it.unibz.pomodroid.SharedListActivity
 */


public class ActivityInventorySheet extends SharedListActivity {

	/* (non-Javadoc)
	 * @see it.unibz.pomodroid.SharedListActivity#onCreate(android.os.Bundle)
	 */
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
	 * This dialog gives the possibility to change the status of each activity
	 * 
	 * @param activity
	 */
	@Override
	protected void openActivityDialog(Activity activity) {
		final Activity selectedActivity = activity;
		new AlertDialog.Builder(this).setTitle(R.string.activity_title)
				.setItems(R.array.ais_dialog,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								try {
									switch (i) {
									case 0:
										selectedActivity.setTodoToday(true);
										selectedActivity.setUndone();
										selectedActivity.save(dbHelper);
										break;
									case 1:
										selectedActivity.close(dbHelper);
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