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

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.factories.ActivityFactory;
import it.unibz.pomodroid.persistency.Service;
import it.unibz.pomodroid.services.TracTicketFetcher;
import it.unibz.pomodroid.services.XmlRpcClient;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class is in charge of retrieving tickets from trac
 * 
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedActivity
 */

public class Services extends SharedActivity implements OnClickListener {

	private ProgressDialog progressDialog = null;
	private Thread serviceThread = null;
	private Vector<HashMap<String, Object>> tracTasks = null;

	private int tracTasksAdded = 0;

	private static int serviceChosen = -1;

	private static final int SERVICE_TRAC = 2;
	private static final int MESSAGE_OK = 1;
	private static final int MESSAGE_EXCEPTION = 2;
	private static final int MESSAGE_INFORMATION = 3;
	private static final int ADD = 4;
	private static final int VIEW = 5;

	private List<Service> services = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibz.pomodroid.SharedActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.services);

		Button buttonTRAC = (Button) findViewById(R.id.ButtonTrac);
		buttonTRAC.setOnClickListener((OnClickListener) this);
		Button buttonEditServices = (Button) findViewById(R.id.ButtonEditServices);
		buttonEditServices.setOnClickListener((OnClickListener) this);
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			this.services = Service.getAllActive(dbHelper);
			String message = "Active Services: ";
			if (this.services != null)
				message += "" + services.size();
			else
				message += "0";
			TextView textViewActiveServices = (TextView) findViewById(R.id.service_active_no);
			textViewActiveServices.setText(message);
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
		menu.add(0, VIEW, 0, "Edit Services").setIcon(
				android.R.drawable.ic_menu_edit);
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
		case VIEW:
			i = new Intent(this, ListServices.class);
			this.startActivity(i);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (XmlRpcClient.isInternetAvailable(context)) {
			if (v.getId() == R.id.ButtonTrac) {
				Services.serviceChosen = SERVICE_TRAC;
			} else if (v.getId() == R.id.ButtonEditServices) {
				Intent intent = new Intent();
				intent.setClass(this, ListServices.class);
				startActivity(intent);
				return;
			} else {
				Services.serviceChosen = -1;
			}
			try {
				useServices();
			} catch (PomodroidException e) {
				e.alertUser(context);
			}

		} else {
			PomodroidException.createAlert(context, "ERROR", context
					.getString(R.string.no_internet_available));
		}
	}

	/**
	 * Method that starts a thread and shows a nice download bar.
	 * 
	 * @throws PomodroidException
	 */
	public void useServices() throws PomodroidException {
		String message = null;
		if (Services.serviceChosen == Services.SERVICE_TRAC) {
			List<Service> services = Service.getAllActive(dbHelper);
			if (services != null && services.size() != 0)
				message = "Downloading Activities from " + services.size()
						+ " active Services";
			else {
				message = "No active Services.";
				return;
			}
		} else {
			message = "Error";
		}
		progressDialog = ProgressDialog.show(this, "Please wait", message,
				true, false);
		// create a new Thread that executes activityRetriever and start it
		this.serviceThread = new Thread(null, useServices, "UserServiceThread");
		this.serviceThread.start();
	}

	/**
	 * As soon as a thread starts, this method is called. If serviceChosen ==
	 * SERVICE_TRAC, it retrieves tickets from TRAC. When the operation is
	 * finished,it sends an empty message to the handler in order to inform the
	 * system that the operation is finished.
	 */
	protected Runnable useServices = new Runnable() {
		@Override
		public void run() {
			if (serviceChosen == SERVICE_TRAC)
				retrieveTicketsFromTrac();
			else
				return;
		}
	};

	/**
	 * This handler waits until the method run() sends an empty message in order
	 * to inform us that the "retrieving phase" is finished.
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			switch (message.what) {
			case Services.MESSAGE_OK:
				progressDialog.dismiss();
				if (serviceChosen == SERVICE_TRAC)
					createDialogTrac();
				else
					return;
				break;
			case Services.MESSAGE_EXCEPTION:
				serviceThread.interrupt();
				progressDialog.dismiss();
				Bundle exceptionBundle = message.getData();
				PomodroidException.createAlert(context, "ERROR",
						exceptionBundle.getString("message"));
				break;
			case Services.MESSAGE_INFORMATION:
				serviceThread.interrupt();
				progressDialog.dismiss();
				Bundle informationBundle = message.getData();
				PomodroidException.createAlert(context, "INFO",
						informationBundle.getString("message"));
				break;

			}
		}

	};

	/**
	 * Method that shows a this.dialog and give the possibility both to retrieve
	 * (infinite times) tickets from trac and exit the activity
	 */
	private void createDialogTrac() {
		String message = null;
		if (this.tracTasksAdded == 0)
			message = "No new Activities";
		else
			message = this.tracTasksAdded + " new activities downloaded";
		PomodroidException.createAlert(context, "INFO", message);
	}

	/**
	 * @throws PomodroidException
	 * 
	 *             This method takes all not-closed tickets from track, then
	 *             inserts them into the local DB.
	 * 
	 */
	private void retrieveTicketsFromTrac() {
		try {
			TracTicketFetcher tracTicketFetcher = new TracTicketFetcher();
			ActivityFactory activityFactory = new ActivityFactory();
			List<Service> services = Service.getAllActive(dbHelper);
			for (Service service : services) {
				this.tracTasks = tracTicketFetcher.fetch(service,
						super.dbHelper);
				this.tracTasksAdded = activityFactory.produce(this.tracTasks,
						super.dbHelper);
			}
		} catch (Exception e) {
			sendMessageHandler(Services.MESSAGE_EXCEPTION, e.toString());
			return;
		}
		sendMessageHandler(Services.MESSAGE_OK, "");
	}

	/**
	 * Sends a customized message to the Handler.
	 * 
	 * @param messageType
	 *            the type of message
	 * @param messageValue
	 *            the text of the message
	 */
	private void sendMessageHandler(int messageType, String messageValue) {
		Message message = new Message();
		if (!messageValue.equals("")) {
			Bundle bundle = new Bundle();
			bundle.putString("message", messageValue);
			message.setData(bundle);
		}
		message.what = messageType;
		handler.sendMessage(message);
	}

}
