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

/**
 * Class to represent common behaviors and aspects of Pomodroid's ListActivities
 * It defines the shared Menu, an ArrayAdapter to represent Activities, and the
 * methods useful to work with the Adapter.
 */
public abstract class SharedListActivity extends ListActivity {
	private static final int AIS = 0;
	private static final int TTS = 1;
	private static final int TS = 2;
	private static final int PRE = 3;
	private static final int SET = 4;

	protected int resourceLayout = -1;

	private Context context = null;
	private ProgressDialog progressDialog = null;
	protected ArrayList<Activity> activities = null;
	protected ActivityAdapter activityAdapter = null;
	protected Runnable activityRetriever = null;
	protected DBHelper dbHelper = null;

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
		this.dbHelper = new DBHelper(this);
		TextView textView = (TextView) findViewById(R.id.activityname);
		textView.setText(R.string.tts);
		this.activities = new ArrayList<Activity>();
		// first call the adapter to show zero Activities
		this.activityAdapter = new ActivityAdapter(this,
				R.layout.ttsactivityentry, activities);
		this.setListAdapter(this.activityAdapter);
		this.context = this;
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
		this.dbHelper.close();
	}

	/**
	 * Default onStop behavior is defined here
	 * 
	 * @see android.app.Activity#onStop(android.os.Bundle)
	 */
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
				TextView tt = (TextView) view.findViewById(R.id.toptext);
				TextView bt = (TextView) view.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText(activity.getShortDescription());
				}
				if (bt != null) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
					bt.setText(R.string.pomodoro_nr + "("
							+ activity.getNumberPomodoro() + ") - "
							+ R.string.deadline + " ("
							+ sdf.format(activity.getDeadline()) + ")");

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

	/**
	 * Overrides Android Menu. This is the menu shared between all our
	 * ListActivities.
	 * 
	 * @param menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, AIS, 0, "Activity Inventory Sheet").setIcon(
				android.R.drawable.ic_menu_upload);
		menu.add(0, TTS, 0, "Todo Today Sheet").setIcon(
				android.R.drawable.ic_menu_upload);
		menu.add(0, TS, 0, "Trash Sheet").setIcon(
				android.R.drawable.ic_menu_delete);
		menu.add(0, PRE, 0, "TRAC/PROM").setIcon(
				android.R.drawable.ic_menu_send);
		menu.add(0, SET, 0, "Settings").setIcon(
				android.R.drawable.ic_menu_preferences);
		return true;
	}

	/**
	 * Defines the actions to be performed when the user clicks on a menu item.
	 * 
	 * @param menu
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
		case AIS:
			i = new Intent(this, ActivityInventorySheet.class);
			startActivity(i);
			return true;
		case TTS:
			i = new Intent(this, TodoTodaySheet.class);
			startActivity(i);
			return true;
		case TS:
			i = new Intent(this, TrashSheet.class);
			startActivity(i);
			return true;
		case PRE:
			i = new Intent(this, TracTicket.class);
			startActivity(i);
			return true;
		case SET:
			i = new Intent(this, Preferences.class);
			startActivity(i);
			return true;
		}
		return false;
	}

}
