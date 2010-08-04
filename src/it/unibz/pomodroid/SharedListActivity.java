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
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.User;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Class to represent common behaviors and aspects of Pomodroid's
 * ListActivities It defines the shared Menu, an ArrayAdapter to
 * represent Activities, and the methods useful to work with the
 * Adapter.
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * 
 * @see adroid.app.ListActivity
 */
public abstract class SharedListActivity extends ListActivity {
	
	public static final int ACTION_ADD_ACTIVITY = 0;
	public static final int ACTION_ADD_SERVICE = 11;
	public static final int ACTION_GO_ABOUT = 2;
	public static final int ACTION_GO_AIS = 3;
	public static final int ACTION_GO_PREFERENCES = 1;
	public static final int ACTION_GO_SERVICES = 6;
	public static final int ACTION_GO_TS = 4;
	public static final int ACTION_GO_TTS = 5;
	public static final int ACTION_LIST_SERVICES = 7;
	public static final int ACTION_REFRESH_SERVICES = 8;
	public static final int ACTION_EMPTY_LIST = 10;
	public static final int MSG_ACTIVITIES_PRESENT = 12;
	public static final int MSG_ACTIVITIES_NOT_PRESENT = 13;
	
	/**
	 * Represents the Android ID of the sub-class preferred layout
	 */
	protected int resourceLayout = -1;
	/**
	 * Contains the Context of the Activity
	 */
	private Context context = null;
	/**
	 * A Progress Dialog to inform the User about the progress of operations
	 */
	private ProgressDialog progressDialog = null;
	/**
	 * Data Structure to hold the activities to be displayed
	 */
	protected ArrayList<Activity> activities = null;
	/**
	 * A Custom Adapter to provide graphical representation for Activities
	 */
	protected ActivityAdapter activityAdapter = null;
	/**
	 * A Runnable that is responsible for retrieving Activities from the DB
	 */
	protected Runnable activityRetriever = null;
	/**
	 * Database container
	 */
	protected DBHelper dbHelper = null;
	
	/**
	 * Current User
	 */
	private User user = null;
	
 
	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}
	
	

	/**
	 * Sets the context. It must be called from a subclass inside onCreate()
	 * 
	 * @param context
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * @return the resource layout
	 */
	public int getResourceLayout() {
		return resourceLayout;
	}

	/**
	 * Sets the layout for displaying Activities. It must be called from a
	 * subclass inside onCreate()
	 * 
	 * @param resourceLayout
	 */
	public void setResourceLayout(int resourceLayout) {
		this.resourceLayout = resourceLayout;
	}

	/**
	 * Default onCreate behavior is defined here
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitysheet);
		this.dbHelper = new DBHelper(getApplicationContext());
		this.context = this;
		this.user = this.getUser();
		this.activities = new ArrayList<Activity>();
		// first call the adapter to show zero Activities
		this.activityAdapter = new ActivityAdapter(this,
				R.layout.ttsactivityentry, activities);
		this.setListAdapter(this.activityAdapter);

	}
	
	public void refreshUser(){
		try {
			setUser(User.retrieve(dbHelper));
		} catch (PomodroidException e) {
			e.alertUser(context);
		}
	}

	/**
	 * Default onResume behavior is defined here
	 * 
	 * @see android.app.Activity#onResume(android.os.Bundle)
	 */
	@Override
	public void onResume() {
		super.onResume();
		try {
			refreshSheet();
		} catch (PomodroidException e) {
			// TODO Auto-generated catch block
			e.alertUser(this);
		}
	}

	/**
	 * Default onPause behavior is defined here
	 * 
	 * @see android.app.Activity#onPause(android.os.Bundle)
	 */
	@Override
	public void onPause() {
		super.onPause();
		this.dbHelper.commit();
	}

	/**
	 * Default onStop behavior is defined here
	 * 
	 * @see android.app.Activity#onStop(android.os.Bundle)
	 */
	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * A customized ArrayAdapter for representing lists of Activities.
	 */
	protected class ActivityAdapter extends ArrayAdapter<Activity> {
		private ArrayList<Activity> items;

		/**
		 * This constructor calls the constructor of ArrayAdapter, setting the
		 * context, the textView id and the items that will hold to be displayed
		 * in the UI
		 * 
		 * @param context
		 * @param textViewResourceId
		 * @param items
		 */
		public ActivityAdapter(Context context, int textViewResourceId,
				ArrayList<Activity> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		/**
		 * This method is responsible to generate each row containing an
		 * Activity.
		 * 
		 * @param position
		 * @param convertView
		 * @param parent
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			// inflate the layout of an activity row in the current view
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(resourceLayout, null);
			}
			final Activity activity = items.get(position);
			// builds the components of the activity entry view
			if (activity != null) {
				TextView textViewTopText = (TextView) view.findViewById(R.id.toptext);
				TextView textViewBottomText = (TextView) view.findViewById(R.id.bottomtext);
				if (getResourceLayout()==R.layout.aisactivityentry && activity.isTodoToday())
				    textViewTopText.setText("TTS: " + activity.getShortSummary());
				else
					textViewTopText.setText(activity.getShortSummary());
				textViewBottomText.setText(context.getString(R.string.pomodoro_nr) 
						+ "(" + activity.getNumberPomodoro() + ") - "
						+ context.getString(R.string.deadline)
						+ " (" + activity.getStringDeadline() + ")");
			}
			view = addListeners(view, activity);
			return view;
		}
	}

	/**
	 * This method attaches listeners to a View, given the Activity associated
	 * to that view.
	 * 
	 * @param view
	 * @param activity
	 * @return view
	 */
	private View addListeners(View view, final Activity activity) {
		// bind a listener to the current Activity row
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivityDialog(activity);
			}
		});
		// bind a listener to the current Activity row
		view.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				createDialog(activity);
				return false;
			}
		});
		return view;
	}

	/**
	 * Creates a dialog that reports useful information about an Activity. To be
	 * used when a User holds a click on an Activity
	 */
	private void createDialog(Activity activity) {
		AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
		dialog.setTitle("Activity Details");
		dialog.setButton("Dismiss", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		String message = context.getString(R.string.reporter) + ": "
				+ activity.getReporter() + "\n"
				+ context.getString(R.string.type) + ": " + activity.getType()
				+ "\n" + context.getString(R.string.priority) + ": "
				+ activity.getPriority() + "\n"
				+ context.getString(R.string.deadline) + ": "
				+ activity.getStringDeadline() + "\n\n"
				+ context.getString(R.string.description) + ": "
				+ activity.getDescription() + "\n";
		dialog.setMessage(message);
		dialog.show();
	}

	/**
	 * Creates a new Runnable object, that is a call to retrieveActivities().
	 * The method is for retrieving activities from the database. It creates a
	 * Thread for the Runnable object, and runs it. Meanwhile, it shows a
	 * ProgressDialog
	 * 
	 * @throws PomodroidException
	 */
	protected void refreshSheet() throws PomodroidException {
		this.activities = new ArrayList<Activity>();
		this.activityAdapter = new ActivityAdapter(this,
				R.layout.trashactivityentry, activities);
		this.setListAdapter(this.activityAdapter);
		this.activityRetriever = new Runnable() {
			@Override
			public void run() {
				// retrieve the Activities from the database
				try {
					retrieveActivities();
				} catch (PomodroidException e) {
					// ugly but necessary
					try {
						throw new PomodroidException(e.getMessage());
					} catch (PomodroidException e1) {

					}
				}
			}
		};
		// create a new Thread that executes activityRetriever and start it
		Thread thread = new Thread(null, activityRetriever,
				"ActivityRetrieverThread");
		thread.start();
		// show a nice progress bar
		progressDialog = ProgressDialog.show(getContext(),
				getString(R.string.plswait), getString(R.string.retactivities),
				true);

	}

	/**
	 * Gets the Activities from Activity.getAll() and adds them to the local
	 * list of activities. It calls populateAdapter to populate the adapter with
	 * the new list of activities MUST be implemented in a subclass, to
	 * successfully select which Activities must be displayed to the user
	 * 
	 * @see it.unibz.pomodroid.persistency.Activity
	 * @throws PomodroidException
	 */
	protected abstract void retrieveActivities() throws PomodroidException;

	protected Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {

			switch (message.what) {
			case MSG_ACTIVITIES_PRESENT:
				findViewById(R.id.empty_sheet).setVisibility(View.INVISIBLE);
				break;
			case MSG_ACTIVITIES_NOT_PRESENT:
				findViewById(R.id.empty_sheet).setVisibility(View.VISIBLE);
				break;
			}
			return;
		}
	};
	
	/**
	 * This thread notifies the adapter of the presence of Activities to be
	 * displayed. It also adds the activities to the adapter, taking them from
	 * the local List of activities
	 */
	protected Runnable populateAdapter = new Runnable() {
		@Override
		public void run() {
			if (activities != null && activities.size() > 0) {
				activityAdapter.notifyDataSetChanged();
				for (int i = 0; i < activities.size(); i++)
					activityAdapter.add(activities.get(i));
			}
			progressDialog.dismiss();
			
			activityAdapter.notifyDataSetChanged();
			
			if(activityAdapter.isEmpty())
				handler.sendEmptyMessage(MSG_ACTIVITIES_NOT_PRESENT);
			else
				handler.sendEmptyMessage(MSG_ACTIVITIES_PRESENT);
			
			
			progressDialog.dismiss();
		}
	};

	/**
	 * This dialog gives the possibility to change the status of each activity
	 * MUST be implemented in a subclass, to successfully determine which
	 * options to show to the user when he clicks on an Activity
	 * 
	 * @param activity
	 */
	protected abstract void openActivityDialog(Activity activity);

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		if(user==null){
			try {
				if(User.isPresent(dbHelper)){
					this.setUser(User.retrieve(dbHelper));
				}else{
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
	 * As soon as the user clicks on the menu a new intent is created for adding new Activity.
	 * @param item
	 * @return
	 * 
	 */
	@Override
	public  boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ACTION_ADD_ACTIVITY:
			startActivity(EditActivity.class, false, true);
			return true;
		case ACTION_ADD_SERVICE:
			startActivity(EditService.class, false, true);
			return true;
		case ACTION_GO_AIS:
			startActivity(ActivityInventorySheet.class, false, true);
			break;
		case ACTION_GO_TTS:
			startActivity(TodoTodaySheet.class, false, true);
			break;
		case ACTION_GO_TS:
			startActivity(TrashSheet.class, false, true);
			break;
		case ACTION_GO_PREFERENCES:
			if (getUser().isAdvanced())
				startActivity(TabPreferences.class, true, true);
			else
				startActivity(Preferences.class, true, true);
			break;
		case ACTION_GO_ABOUT:
			startActivity(About.class, false, true);
			break;
		case ACTION_EMPTY_LIST:
			for(int i=0;i<activityAdapter.getCount();i++){
				Activity activity = activityAdapter.getItem(i);
				try {
					Event.delete(activity, dbHelper);
					activity.delete(dbHelper);
				} catch (PomodroidException e) {
					e.alertUser(this);
				}
			}
			activityAdapter.clear();
			activityAdapter.notifyDataSetChanged();
			return true;
		}
		return false;
	}
	
	public void startActivity(Class<?> klass, boolean finishCurrentActivity, boolean recycleActivity){
		Intent intent = new Intent(this.context, klass);
		if(recycleActivity)
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		if(finishCurrentActivity)
			finish();
	}

}
