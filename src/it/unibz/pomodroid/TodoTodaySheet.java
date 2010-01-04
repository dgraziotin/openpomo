package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author bodom_lx
 * 
 */
public class TodoTodaySheet extends ListActivity {
	
	protected static final int SUB_ACTIVITY_REQUEST_CODE = 666;
	private ProgressDialog progressDialog = null;
	private ArrayList<Activity> activities = null;
	private ActivityAdapter activityAdapter = null;
	private Runnable activityRetriever = null;
	private DBHelper dbHelper = null;
	private Context context = null;

	/**
	 * A customized ArrayAdapter for representing lists of Activities.
	 */
	private class ActivityAdapter extends ArrayAdapter<Activity> {
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			// inflate the layout of an activity row in the current view
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.ttsactivityentry, null);
			}
			final Activity activity = items.get(position);
			// builds the components of the activity entry view
			if (activity != null) {
				TextView tt = (TextView) view.findViewById(R.id.toptext);
				TextView bt = (TextView) view.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText(activity.getShortDescription());
				}
				if (bt != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
					bt.setText("Pomodoro#(" + activity.getNumberPomodoro() + ") - DeadLine (" + sdf.format(activity.getDeadline()) + ")");
				
				}
			}
			// bind a listener to the current Activity row
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i("TTS", "Clicked Activity: " + activity.getOriginId());
					openActivityDialog(activity);
				}
			});
			return view;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitysheet);
		this.dbHelper = new DBHelper(this);
		this.activities = new ArrayList<Activity>();
		// first call the adapter to show zero Activities
		this.activityAdapter = new ActivityAdapter(this,
				R.layout.ttsactivityentry, activities);
		this.setListAdapter(this.activityAdapter);
		this.context = this;
	}

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

	@Override
	public void onPause() {
		super.onPause();
		this.dbHelper.close();
	}

	public void onStop() {
		super.onStop();
		this.dbHelper.close();
	}

	/**
	 * Creates a new Runnable object, that is a call to retrieveActivities().
	 * The method is for retrieving activities from the database. It creates a
	 * Thread for the Runnable object, and runs it. Meanwhile, it shows a
	 * ProgressDialog
	 * 
	 * @throws PomodroidException
	 */
	private void refreshSheet() throws PomodroidException {
		this.activities = new ArrayList<Activity>();
		this.activityAdapter = new ActivityAdapter(this,
				R.layout.ttsactivityentry, activities);
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
		progressDialog = ProgressDialog.show(TodoTodaySheet.this,
				"Please wait...", "Retrieving activities ...", true);

	}

	/**
	 * Gets the Activities from Activity.getAll() and adds them to the local
	 * list of activities. It calls populateAdapter to populate the adapter with
	 * the new list of activities
	 * 
	 * @see it.unibz.pomodroid.persistency.Activity
	 * @throws PomodroidException
	 */
	private void retrieveActivities() throws PomodroidException {
		try {
			activities = new ArrayList<Activity>();
			List<Activity> retrievedActivities = Activity
					.getTodoToday(this.dbHelper);
			activities.addAll(retrievedActivities);
			Log.i("AIS.getActivities(): activities retrieved:", ""
					+ activities.size());
		} catch (Exception e) {
			throw new PomodroidException(
					"Error in retrieving Activities from the DB! Please try again");
		}
		this.runOnUiThread(populateAdapter);
	}

	/**
	 * This thread notifies the adapter of the presence of Activities to be
	 * displayed. It also adds the activities to the adapter, taking them from
	 * the local List of activities
	 */
	private Runnable populateAdapter = new Runnable() {
		@Override
		public void run() {
			if (activities != null && activities.size() > 0) {
				activityAdapter.notifyDataSetChanged();
				for (int i = 0; i < activities.size(); i++)
					activityAdapter.add(activities.get(i));
			}
			progressDialog.dismiss();
			activityAdapter.notifyDataSetChanged();
		}
	};

	
	/**
	 * This dialog gives the possibility to change the status of each activity
	 * 
	 * @param activity
	 */
	private void openActivityDialog(Activity activity){
		final Activity selectedActivity = activity;
		new AlertDialog.Builder(this)
	         .setTitle(R.string.activity_title) 
	         .setItems(R.array.tts_dialog,
			  new DialogInterface.OnClickListener() { 
			  	 public void onClick(DialogInterface dialoginterface, int i) {
			  	   try {
			  		 switch (i) {
			  		    case 0: Log.i("TTS.openactivityDialog()"," TTS starting pomodoro");
			  					Intent intent = new Intent();
			  					intent.setClass(getApplicationContext(), Pomodoro.class);
			  					Bundle b = new Bundle();
			  					b.putString("origin", selectedActivity.getOrigin());
			  					b.putInt("originId", selectedActivity.getOriginId());
			  					intent.putExtras(b);
			  					startActivity(intent);
			  					//startActivityForResult(intent,SUB_ACTIVITY_REQUEST_CODE);
			  		    		break;
			  		 	case 1: Log.i("TTS.openactivityDialog()"," TTS setting todotoday false");
			  		    		selectedActivity.setTodoToday(false);
			  		    		selectedActivity.setUndone();
			  		    		selectedActivity.save(dbHelper);
			  		    		activityAdapter.remove(selectedActivity);
			  		            break;
			  		    case 2: Log.i("TTS.openactivityDialog()"," TTS setting done");
			  		    		selectedActivity.close(dbHelper);
			  		    		activityAdapter.remove(selectedActivity);
			  		            break;
			  		 }
			  	   } catch (PomodroidException e) {
			  		 Log.e("TTS.openActivityDialog()","Error: " + e.getMessage());
			  		 e.alertUser(context);
			  	   } finally{
			  		   dbHelper.close();
			  	   }
			     } 
	          })
	    .show();
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == SUB_ACTIVITY_REQUEST_CODE){
				Bundle b = data.getExtras();
				TextView textView = (TextView) findViewById(R.id.hello);
				textView.setText(b.getString("TEXT"));
        }
	}

}
