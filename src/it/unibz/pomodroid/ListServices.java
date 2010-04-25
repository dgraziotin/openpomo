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
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
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
 * Class to represent common behaviors and aspects of Pomodroid's
 * ListActivities It defines the shared Menu, an ArrayAdapter to
 * represent Activities, and the methods useful to work with the
 * Adapter.
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * 
 * @see adroid.app.ListActivity
 */
public class ListServices extends ListActivity {
	private ProgressDialog progressDialog = null;
    private ArrayList<Service> services = null;
    private ServiceAdapter adapter;
    private Runnable viewServices;
    private DBHelper dbHelper;
    private Context context;
    private static final int ADD = 0;
	private static final int REFRESH = 1;
   
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listservices);
        dbHelper = new DBHelper(this);
        this.context = this;
        services = new ArrayList<Service>();
        this.adapter = new ServiceAdapter(this, R.layout.serviceentry, services);
        setListAdapter(this.adapter);
        retrieveServices();
    }
    
    private void retrieveServices(){
    	this.services = new ArrayList<Service>();
		this.adapter = new ServiceAdapter(this,
				R.layout.serviceentry, services);
		this.setListAdapter(this.adapter);
		this.viewServices = new Runnable() {
			@Override
			public void run() {
				try {
					getServices();
				} catch (PomodroidException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		// create a new Thread that executes activityRetriever and start it
		Thread thread = new Thread(null, viewServices,
				"ServiceRetrieverThread");
		thread.start();
		// show a nice progress bar
		progressDialog = ProgressDialog.show(this,
				getString(R.string.plswait), getString(R.string.service_retrieve),
				true);

    }
    
    /**
	 * We specify the menu labels and theirs icons
	 * @param menu
	 * @return
	 *
	 */
	@Override
	public  boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ADD, 0, "Add a new Service").setIcon(
				android.R.drawable.ic_menu_add);
		menu.add(0, REFRESH, 0, "Refresh").setIcon(
				R.drawable.ic_menu_refresh);
		return true;
	}

	/**
	 * As soon as the user click on the menu a new intent is created
	 * @param item
	 * @return
	 * 
	 */
	@Override
	public  boolean onOptionsItemSelected(MenuItem item) {
		Intent i; 
		switch (item.getItemId()) {
		case ADD:
			i = new Intent(this, EditService.class);
			this.startActivity(i);
			return true;
		case REFRESH:
			retrieveServices();
			return true;
		
		}
		return false;
	}
    
    private void getServices() throws PomodroidException{
        	try {
    			services = new ArrayList<Service>();
    			List<Service> retrievedServices = Service.getAll(dbHelper);
    			services.addAll(retrievedServices);
    		} catch (Exception e) {
    			throw new PomodroidException("Error in retrieving Activities from the DB!");
    		}
    		this.runOnUiThread(returnRes);
          
      }
    
    /**
	 * This thread notifies the adapter of the presence of Activities to be
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

    
    private class ServiceAdapter extends ArrayAdapter<Service> {

        private ArrayList<Service> items;

        public ServiceAdapter(Context context, int textViewResourceId, ArrayList<Service> items) {
                super(context, textViewResourceId, items);
                this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.serviceentry, null);
                }
                Service service = items.get(position);
                if (service != null) {
                        TextView tt = (TextView) v.findViewById(R.id.toptext);
                        TextView bt = (TextView) v.findViewById(R.id.bottomtext);
                        if (tt != null) {
                              tt.setText("Name: "+service.getName());                            }
                        if(bt != null){
                        	  Boolean isActive = service.isActive();
                              bt.setText("Type: "+ service.getType() +" Active: "+ isActive.toString());
                        }
                }
                v = addListeners(v, service);
                return v;
        }
        
    	/**
    	 * This dialog gives the possibility to change the status of each activity
    	 * 
    	 * @param activity
    	 */
    	
    	protected void openServiceDialog(final Service service) {
    		new AlertDialog.Builder(context).setTitle("")
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
    										bundle.putString("serviceName", service.getName());
    										intent.putExtras(bundle);
    										startActivity(intent);
    										break;
    									case 1:
    										service.setActive(!service.isActive());
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
    	 * This method attaches listeners to a View, given the Service associated
    	 * to that view.
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
