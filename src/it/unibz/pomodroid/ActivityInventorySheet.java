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
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ActivityInventorySheet extends ListActivity {

	private ProgressDialog progressDialog = null;
	private ArrayList<Activity> activities = null;
	private ActivityAdapter adapter = null;
	private Runnable viewActivities = null;
	private DBHelper dbHelper = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitysheet);
		dbHelper = new DBHelper(this);
		activities = new ArrayList<Activity>();
		this.adapter = new ActivityAdapter(this, R.layout.activityentry,
				activities);
		setListAdapter(this.adapter);

		viewActivities = new Runnable() {
			@Override
			public void run() {
				getActivities();
			}
		};
		Thread thread = new Thread(null, viewActivities, "MagentoBackground");
		thread.start();
		progressDialog = ProgressDialog.show(ActivityInventorySheet.this,
				"Please wait...", "Retrieving data ...", true);
	}

	@Override
	public void onPause() {
		this.dbHelper.close();
		super.onPause();
	}

	private class ActivityAdapter extends ArrayAdapter<Activity> {

		private ArrayList<Activity> items;

		public ActivityAdapter(Context context, int textViewResourceId,
				ArrayList<Activity> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = layoutInflater.inflate(R.layout.activityentry, null);
			}
			Activity activity = items.get(position);
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
			return view;
		}
	}

	private void getActivities() {
		try {
			activities = new ArrayList<Activity>();
			List<Activity> retrievedActivities = Activity.getAll(this.dbHelper);
			activities.addAll(retrievedActivities);
			Log.i("AIS.getActivities(): activities retrieved:", ""
					+ activities.size());
		} catch (Exception e) {
			Log.e("AIS.getActivities() thread: ", e.getMessage());
		}
		this.runOnUiThread(activitiesFetcher);
	}

	private Runnable activitiesFetcher = new Runnable() {

		@Override
		public void run() {
			if (activities != null && activities.size() > 0) {
				adapter.notifyDataSetChanged();
				for (int i = 0; i < activities.size(); i++)
					adapter.add(activities.get(i));
			}
			progressDialog.dismiss();
			adapter.notifyDataSetChanged();
		}
	};
}