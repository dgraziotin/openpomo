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

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.Service;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Class for representing the list of stored Services in Pomodroid.
 * 
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * 
 * @see adroid.app.ListActivity
 * 
 */
public class ListServices extends ListActivity {
	/**
	 * The progress dialog to inform the user about the status of the operations
	 * done
	 */
	private ProgressDialog progressDialog = null;
	/**
	 * Holds the stored Services
	 */
	private ArrayList<Service> services = null;
	/**
	 * Custom adapter to provide Services to the Layout
	 */
	private ServiceAdapter adapter;
	/**
	 * Runnable responsible for retrieving Services and refreshing the list
	 */
	private Runnable viewServices;
	/**
	 * Database container
	 */
	private DBHelper dbHelper;
	/**
	 * Current context of the Activity
	 */
	private Context context;
	/**
	 * Represents the intention of the user to add a new Service
	 */
	private static final int ACTION_ADD = 0;
	/**
	 * Represents the intention of the user to refresh the list of Services
	 */
	private static final int ACTION_REFRESH = 1;

	/**
	 * Loads the layout and sets some attributes. It prepares the Database
	 * container and the custom Adapter
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listservices);
		dbHelper = new DBHelper(this);
		this.context = this;
		services = new ArrayList<Service>();
		this.adapter = new ServiceAdapter(this, R.layout.serviceentry, services);
		setListAdapter(this.adapter);
	}

	/**
	 * Refreshes the list of Services when the Activity gains focus
	 */
	@Override
	public void onResume() {
		super.onResume();
		retrieveServices();
	}

	/**
	 * Sets the custom Adapter, prepares the Runnable for getting Services,
	 * creates and runs the Thread with the Runnable
	 */
	private void retrieveServices() {
		this.services = new ArrayList<Service>();
		this.adapter = new ServiceAdapter(this, R.layout.serviceentry, services);
		this.setListAdapter(this.adapter);
		this.viewServices = new Runnable() {
			@Override
			public void run() {
				try {
					getServices();
				} catch (PomodroidException e) {
					e.alertUser(context);
				}
			}
		};
		// create a new Thread that executes activityRetriever and start it
		Thread thread = new Thread(null, viewServices, "ServiceRetrieverThread");
		thread.start();
		// show a nice progress bar
		progressDialog = ProgressDialog.show(this, getString(R.string.plswait),
				getString(R.string.service_retrieve), true);

	}

	/**
	 * We specify the menu labels and their icons
	 * 
	 * @param menu
	 * @return true
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ACTION_ADD, 0, "Add a new Service").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(0, ACTION_REFRESH, 0, "Refresh").setIcon(
				R.drawable.ic_menu_refresh);
		return true;
	}

	/**
	 * As soon as the user clicks on the menu a new intent is created for adding
	 * new Service. Otherwise we refresh the list of Services
	 * 
	 * @param item
	 * @return
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = new Intent();
		switch (item.getItemId()) {
		case ACTION_ADD:
			intent.setClass(context, EditService.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			return true;
		case ACTION_REFRESH:
			retrieveServices();
			return true;

		}
		return false;
	}

	/**
	 * Retrieves Services from Database and calls a Runnable responsible for
	 * notifying the Adapter of a new set of data to be represented graphically.
	 * 
	 * @throws PomodroidException
	 */
	private void getServices() throws PomodroidException {
		try {
			services = new ArrayList<Service>();
			List<Service> retrievedServices = Service.getAll(dbHelper);
			services.addAll(retrievedServices);
		} catch (Exception e) {
			throw new PomodroidException(
					"Error in retrieving Activities from the DB!");
		}
		this.runOnUiThread(returnRes);

	}

	/**
	 * This Runnable notifies the adapter of the presence of Activities to be
	 * displayed. It also adds the activities to the adapter, taking them from
	 * the local List of activities
	 */
	protected Runnable returnRes = new Runnable() {
		@Override
		public void run() {
			if (services != null && services.size() > 0) {
				adapter.notifyDataSetChanged();
				for (int i = 0; i < services.size(); i++)
					adapter.add(services.get(i));
			}
			progressDialog.dismiss();
			adapter.notifyDataSetChanged();
		}
	};

	/**
	 * Private class implementing a custom ArrayAdapter to provide graphical
	 * representation for our stored Services
	 * 
	 * @see android.widget.ArrayAdapter
	 */
	private class ServiceAdapter extends ArrayAdapter<Service> {

		/**
		 * The list of Services
		 */
		private ArrayList<Service> items;

		public ServiceAdapter(Context context, int textViewResourceId,
				ArrayList<Service> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		/**
		 * Gets the layout created for representing an entry in the Service
		 * list, attach listeners, provides custom text and inflates the layout
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.serviceentry, null);
			}
			Service service = items.get(position);
			if (service != null) {
				TextView tt = (TextView) v.findViewById(R.id.toptext);
				TextView bt = (TextView) v.findViewById(R.id.bottomtext);
				if (tt != null) {
					tt.setText("Name: " + service.getName());
				}
				if (bt != null) {
					Boolean isActive = service.isActive();
					bt.setText("Type: " + service.getType() + " Active: "
							+ isActive.toString());
				}
			}
			v = addListeners(v, service);
			return v;
		}

		/**
		 * This dialog gives the possibility select an action for a selected
		 * service
		 * 
		 * @param activity
		 */
		protected void openServiceDialog(final Service service) {
			new AlertDialog.Builder(context)
					.setTitle("")
					.setItems(R.array.service_dialog,
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									try {
										switch (i) {
										case 0:
											Intent intent = new Intent();
											intent.setClass(
													getApplicationContext(),
													EditService.class);
											Bundle bundle = new Bundle();
											bundle.putString("serviceName",
													service.getName());
											intent.putExtras(bundle);
											intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
											startActivity(intent);
											break;
										case 1:
											service.setActive(!service
													.isActive());
											service.save(dbHelper);
											adapter.notifyDataSetChanged();
											break;
										case 2:
											service.delete(dbHelper);
											adapter.remove(service);
											adapter.notifyDataSetChanged();
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

		/**
		 * This method attaches listeners to a View, given the Service
		 * associated to that view.
		 * 
		 * @param view
		 * @param activity
		 * @return view
		 */
		private View addListeners(View view, final Service service) {
			// bind a listener to the current Activity row
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					openServiceDialog(service);
				}
			});
			return view;
		}

	}
}
