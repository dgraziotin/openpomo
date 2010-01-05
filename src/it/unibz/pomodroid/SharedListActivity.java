package it.unibz.pomodroid;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public abstract class SharedListActivity extends ListActivity
{
	private static final int AIS = 0;
	private static final int TTS = 1;
	private static final int TS  = 2;
	private static final int PRE = 3;
	private static final int SET = 4;
	
	protected int resourceLayout = -1;
	
	protected ProgressDialog progressDialog = null;
	protected ArrayList<Activity> activities = null;
	protected ActivityAdapter activityAdapter = null;
	protected Runnable activityRetriever = null;
	protected DBHelper dbHelper = null;
	private Context context = null;
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public int getResourceLayout() {
		return resourceLayout;
	}

	public void setResourceLayout(int resourceLayout) {
		this.resourceLayout = resourceLayout;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitysheet);
		this.dbHelper = new DBHelper(this);
		TextView textView = (TextView) findViewById(R.id.activityname);
		textView.setText(R.string.tts);
		this.activities = new ArrayList<Activity>();
		// first call the adapter to show zero Activities
		this.activityAdapter = new ActivityAdapter(this,R.layout.ttsactivityentry, activities);
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
				TextView tt = (TextView) view.findViewById(R.id.toptext);
				TextView bt = (TextView) view.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText(activity.getShortDescription());
				}
				if (bt != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
					bt.setText(R.string.pomodoro_nr + "(" + activity.getNumberPomodoro() + ") - "+ R.string.deadline +" (" + sdf.format(activity.getDeadline()) + ")");
				
				}
			}
			// bind a listener to the current Activity row
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					openActivityDialog(activity);
				}
			});
			return view;
		}
	}
	
	/**
	 * Creates a new Runnable object, that is a call to retrieveActivities().
	 * The method is for retrieving activities from the database. It creates a
	 * Thread for the Runnable object, and runs it. Meanwhile, it shows a
	 * ProgressDialog
	 * 
	 * @throws PomodroidException
	 */
	protected abstract void refreshSheet() throws PomodroidException;

	/**
	 * Gets the Activities from Activity.getAll() and adds them to the local
	 * list of activities. It calls populateAdapter to populate the adapter with
	 * the new list of activities
	 * 
	 * @see it.unibz.pomodroid.persistency.Activity
	 * @throws PomodroidException
	 */
	protected abstract void retrieveActivities() throws PomodroidException;


	
	/**
	 * This dialog gives the possibility to change the status of each activity
	 * 
	 * @param activity
	 */
	protected abstract void openActivityDialog(Activity activity);
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, AIS, 0, "Activity Inventory Sheet").setIcon(android.R.drawable.ic_menu_upload);
		menu.add(0, TTS, 0, "Todo Today Sheet").setIcon(android.R.drawable.ic_menu_upload);
		menu.add(0, TS,  0, "Trash Sheet").setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, PRE, 0, "TRAC/PROM").setIcon(android.R.drawable.ic_menu_send);
		menu.add(0, SET, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}
	
	public  boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
	    switch (item.getItemId()) {
	    case AIS: i = new Intent(this, ActivityInventorySheet.class);	
	    	startActivity(i);
	              return true;
	    case TTS: i = new Intent(this, TodoTodaySheet.class);
		          startActivity(i);
                  return true;
	    case TS:  i = new Intent(this, TrashSheet.class);
        		  startActivity(i);
        		  return true;
	    case PRE: i = new Intent(this, TracTicket.class);
		  		  startActivity(i);
		          return true;
	    case SET: i = new Intent(this, Preferences.class);
	    		  startActivity(i);
	    		  return true;
	    }
	    return false;
	}
   
}

