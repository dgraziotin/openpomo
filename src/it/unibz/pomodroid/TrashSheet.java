package it.unibz.pomodroid;

import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;
import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author bodom_lx
 * 
 */
public class TrashSheet extends ListActivity {

	private ProgressDialog progressDialog = null;
	private ArrayList<Activity> activities = null;
	private ActivityAdapter activityAdapter = null;
	private Runnable activityRetriever = null;
	private DBHelper dbHelper = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitysheet);
		this.dbHelper = new DBHelper(this);
		this.activities = new ArrayList<Activity>();

		// first call the adapter to show zero Activities
		this.activityAdapter = new ActivityAdapter(this,
				R.layout.activityentry, activities);
		this.setListAdapter(this.activityAdapter);

		// create a new Runnable to hold the methods to be executed in a Thread
		this.activityRetriever = new Runnable() {
			@Override
			public void run() {
				// retrieve the Activities from the database
				retrieveActivities();
			}
		};

		// create a new Thread that executes activityRetriever and start it
		Thread thread = new Thread(null, activityRetriever,
				"ActivityRetrieverThread");
		thread.start();
		// show a nice progress bar
		progressDialog = ProgressDialog.show(TrashSheet.this,
				"Please wait...", "Retrieving activities ...", true);
	}

	@Override
	public void onPause() {
		this.dbHelper.close();
		super.onPause();
	}

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
				view = layoutInflater.inflate(R.layout.activityentry, null);
			}
			final Activity activity = items.get(position);
			// builds the components of the activity entry view
			if (activity != null) {
				TextView tt = (TextView) view.findViewById(R.id.toptext);
				TextView bt = (TextView) view.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText("Name: " + activity.getDescription());
				}
				if (bt != null) {
					bt.setText("Status: " + activity.getOrigin());
				}
			}
			// bind a listener to the current Activity row
			view.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Log.i("TTS", "Clicked Activity: " + activity.getOriginId());
					return false;
				}
			});
			return view;
		}
	}

	/**
	 * Gets the Activities from Activity.getAll() and adds them to the local
	 * list of activities. It calls populateAdapter to populate the adapter with
	 * the new list of activities
	 * 
	 * @see it.unibz.pomodroid.persistency.Activity
	 */

	private void retrieveActivities() {
		try {
			activities = new ArrayList<Activity>();
			List<Activity> retrievedActivities = Activity.getCompleted(this.dbHelper);
			activities.addAll(retrievedActivities);
			Log.i("AIS.getActivities(): activities retrieved:", ""
					+ activities.size());
		} catch (Exception e) {
			Log.e("AIS.getActivities() thread: ", e.getMessage());
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
}