package it.unibz.pomodroid;

import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;

import java.util.ArrayList;
import java.util.Collection;
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
		setContentView(R.layout.activityinventorysheet);
		dbHelper = new DBHelper(this);
		activities = new ArrayList<Activity>();
		this.adapter = new ActivityAdapter(this, R.layout.row, activities);
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
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}
			Activity o = items.get(position);
			if (o != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText("Name: " + o.getDescription());
				}
				if (bt != null) {
					bt.setText("Status: " + o.getOrigin());
				}
			}
			return v;
		}
	}

	private void getActivities() {
		try {
			activities = new ArrayList<Activity>();
			List<Activity> retrievedActivities = Activity.getAll(this.dbHelper);
			activities.addAll(retrievedActivities);

			Log.i("ARRAY", "" + activities.size());
		} catch (Exception e) {
			Log.e("BACKGROUND_PROC", e.getMessage());
		}
		runOnUiThread(returnRes);
	}

	private Runnable returnRes = new Runnable() {

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