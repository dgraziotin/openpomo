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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RelativeLayout;

/**
 * Todo Today Sheet class is an extension of Shared List activity. This class
 * shows to the user which activities have to be done today.
 * 
 * Here we can face the activity, move it into the activity inventory sheet or
 * into the trash sheet.
 * 
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedListActivity
 */
public class TodoTodaySheet extends SharedListActivity {

	/**
	 * @see it.unibz.pomodroid.SharedListActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.setResourceLayout(R.layout.ttsactivityentry);
		super.setContext(this);
		super.onCreate(savedInstanceState);

		TextView textViewEmptySheet = (TextView) findViewById(R.id.empty_sheet);
		textViewEmptySheet.setText(getString(R.string.no_activities_tts));
		textViewEmptySheet.setVisibility(View.INVISIBLE);
		Button buttonQuickInsertActivity = (Button) findViewById(R.id.buttonquickinsertactivity);
		final EditText editTextQuickInsertActivity = (EditText) findViewById(R.id.EditTextQuickInsertActivity);
		buttonQuickInsertActivity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editTextQuickInsertActivity.getText().toString().equals(""))
					return;
				Calendar calendar = new GregorianCalendar();
				try {
					Activity activity = new Activity(0, new Date(), calendar
							.getTime(), editTextQuickInsertActivity.getText()
							.toString(), "", "local", Activity
							.getLastLocalId(dbHelper) + 1, "medium", "you",
							"task");
					activity.setTodoToday(true);
					activity.save(dbHelper);
					findViewById(R.id.empty_sheet)
							.setVisibility(View.INVISIBLE);
					refreshSheet();

					throw new PomodroidException("INFO: Activity saved.");
				} catch (PomodroidException e) {
					e.alertUser(getContext());
				}

			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (super.getUser().isQuickInsertActivity()) {
			RelativeLayout activityList = (RelativeLayout) findViewById(R.id.activitylist);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 0, 50);

			activityList.setLayoutParams(layoutParams);

			RelativeLayout quickActivityInsert = (RelativeLayout) findViewById(R.id.quickinsertactivity);
			quickActivityInsert.setVisibility(View.VISIBLE);

		} else {
			RelativeLayout activityList = (RelativeLayout) findViewById(R.id.activitylist);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 0, 0);

			activityList.setLayoutParams(layoutParams);

			RelativeLayout quickActivityInsert = (RelativeLayout) findViewById(R.id.quickinsertactivity);
			quickActivityInsert.setVisibility(View.GONE);
		}
	}

	/**
	 * Gets the Activities from Activity.getTodoToday() and adds them to the
	 * local list of activities. It calls populateAdapter to populate the
	 * adapter with the new list of activities
	 * 
	 * @see it.unibz.pomodroid.persistency.Activity
	 * @throws PomodroidException
	 */
	@Override
	protected void retrieveActivities() throws PomodroidException {
		try {
			activities = new ArrayList<Activity>();
			List<Activity> retrievedActivities = Activity
					.getTodoToday(this.dbHelper);
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
		if (super.getUser().isAdvanced()) {
			ttsDialog = R.array.tts_dialog;
			new AlertDialog.Builder(this).setTitle(R.string.activity_title)
					.setItems(ttsDialog, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int i) {
							try {
								switch (i) {
								case 0:
									Intent intent = new Intent();
									intent.setClass(getApplicationContext(),
											Pomodoro.class);
									Bundle bundle = new Bundle();
									bundle.putString("origin",
											selectedActivity.getOrigin());
									bundle.putInt("originId",
											selectedActivity.getOriginId());
									intent.putExtras(bundle);
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
									break;
								case 1:
									selectedActivity.close(dbHelper);
									activityAdapter.remove(selectedActivity);
									break;
								case 2:
									selectedActivity.setTodoToday(false);
									selectedActivity.setUndone();
									selectedActivity.save(dbHelper);
									activityAdapter.remove(selectedActivity);
									break;
								}
							} catch (PomodroidException e) {
								e.alertUser(getContext());
							} finally {
								dbHelper.commit();
							}
						}
					}).show();
		} else {
			ttsDialog = R.array.tts_dialog_simple;
			new AlertDialog.Builder(this).setTitle(R.string.activity_title)
					.setItems(ttsDialog, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface,
								int i) {
							try {
								switch (i) {
								case 0:
									Intent intent = new Intent();
									intent.setClass(getApplicationContext(),
											Pomodoro.class);
									Bundle bundle = new Bundle();
									bundle.putString("origin",
											selectedActivity.getOrigin());
									bundle.putInt("originId",
											selectedActivity.getOriginId());
									intent.putExtras(bundle);
									intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent);
									break;
								case 1:
									selectedActivity.close(dbHelper);
									activityAdapter.remove(selectedActivity);
									break;
								case 2:
									if (!selectedActivity.getOrigin().equals("local")){
										PomodroidException.createAlert(getContext(), "INFO", "You cannot edit Activities remotely retrieven.");
									}
									Intent intent2 = new Intent();
									intent2.setClass(getContext(),
											EditActivity.class);
									Bundle bundle2 = new Bundle();
									bundle2.putInt("originId",
											selectedActivity.getOriginId());
									intent2.putExtras(bundle2);
									intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
									startActivity(intent2);
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

	/**
	 * We specify the menu labels and theirs icons
	 * 
	 * @param menu
	 * @return true
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (super.getUser().isAdvanced()) {
			menu.add(0, ACTION_ADD_ACTIVITY, 0, "New Activity").setIcon(
					android.R.drawable.ic_menu_add);
			menu.add(0, ACTION_GO_TS, 0, "Trash Can").setIcon(
					android.R.drawable.ic_menu_delete);
			menu.add(0, ACTION_GO_PREFERENCES, 0, "Preferences").setIcon(
					android.R.drawable.ic_menu_preferences);
			menu.add(0, ACTION_GO_ABOUT, 0, "About").setIcon(
					android.R.drawable.ic_menu_help);
			return true;
		}
		menu.add(0, ACTION_ADD_ACTIVITY, 0, "New Activity").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(0, ACTION_GO_TS, 0, "Trash Can").setIcon(
				android.R.drawable.ic_menu_delete);
		menu.add(0, ACTION_GO_PREFERENCES, 0, "Preferences").setIcon(
				android.R.drawable.ic_menu_preferences);
		menu.add(0, ACTION_GO_ABOUT, 0, "About").setIcon(
				android.R.drawable.ic_menu_help);
		return true;
	}

}
