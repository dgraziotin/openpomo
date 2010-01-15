package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.factories.ActivityFactory;
import it.unibz.pomodroid.factories.PromFactory;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.services.PromEventDeliverer;
import it.unibz.pomodroid.services.TrackTicketFetcher;
import it.unibz.pomodroid.services.XmlRpcClient;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.xmlrpc.android.XMLRPCException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This class is in charge of retrieving tickets from trac and seding events to
 * prom.
 * 
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedActivity
 */

public class Services extends SharedActivity implements OnClickListener {

	private ProgressDialog progressDialog = null;
	private AlertDialog dialog;
	private Thread serviceThread = null;

	private byte[] zipIni = null;
	private Vector<HashMap<String, Object>> tasks = null;

	private int taskAdded = 0;
	private int numberEvents = 0;
	private String message = null;
	private List<Event> events = null;

	private static final int PROM = 1;
	private static final int TRAC = 2;
	private static int source = -1;

	private static final int MESSAGE_OK = 1;
	private static final int MESSAGE_EXCEPTION = 2;
	private static final int MESSAGE_INFORMATION = 3;

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibz.pomodroid.SharedActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.services);
		PromFactory promFactory = new PromFactory();
		try {
			events = Event.getAll(super.dbHelper);
			if (!(events == null || events.size() == 0)) {
				numberEvents = events.size();
				this.zipIni = promFactory.createZip(events, super.user);
			}
		} catch (PomodroidException e) {
			e.alertUser(super.context);
		}

		Button buttonTRAC = (Button) findViewById(R.id.ButtonTrac);
		buttonTRAC.setOnClickListener((OnClickListener) this);
		Button buttonProm = (Button) findViewById(R.id.ButtonProm);
		buttonProm.setOnClickListener((OnClickListener) this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		this.serviceThread.interrupt();
		this.serviceThread.stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		if (XmlRpcClient.isInternetAvailable(context)){
			if (v.getId() == R.id.ButtonTrac)
				source = TRAC;
			else
				source = PROM;
			useServices();
		}else{
			try {
				throw new PomodroidException("No Internet connection available. Please try again when a Connection is available.");
			} catch (PomodroidException e) {
				e.alertUser(context);
			}
		}
	}

	/**
	 * Method that starts a thread and shows a nice downloading bar.
	 */
	public void useServices() {
		String message = null;
		if (source == PROM){
			message = "Sending Data to PROM";
		}else{
			message = "Downloading tickets from TRAC";
		}
		progressDialog = ProgressDialog.show(this, "Please wait", message,
				true, false);
		// create a new Thread that executes activityRetriever and start it
		this.serviceThread = new Thread(null, useServices, "UserServiceThread");
		this.serviceThread.start();
	}

	protected Runnable useServices = new Runnable() {
		/**
		 * As soon as a thread starts, this method is called. If source == PROM,
		 * it sends the events to PROM If source == TRAC, it retrieves tickets
		 * from TRAC. When the operation is finished,it sends an empty message
		 * to the handler in order to inform the system that the operation is
		 * finished.
		 */
		@Override
		public void run() {
			if (source == PROM)
				sendPromEvents();
			else
				retrieveTicketsFromTrac();
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
				if (source == PROM)
					createDialogProm();
				else
					createDialogTrac();
				break;
			case Services.MESSAGE_EXCEPTION:
				serviceThread.interrupt();
				progressDialog.dismiss();
				Bundle exceptionBundle = message.getData();
				PomodroidException.createAlert(context, "ERROR", exceptionBundle.getString("message"));
				break;
			case Services.MESSAGE_INFORMATION:
				serviceThread.interrupt();
				progressDialog.dismiss();
				Bundle informationBundle = message.getData();
				PomodroidException.createAlert(context, "INFO", informationBundle.getString("message"));
				break;

			}
		}

	};

	/**
	 * Method that shows a this.dialog and give the possibility both to retrieve
	 * (infinite times) tickets from trac and exit the activity
	 */
	private void createDialogTrac() {
		this.dialog = new AlertDialog.Builder(this).create();
		this.dialog.setTitle("Message");

		if (this.taskAdded == 0)
			this.message = "No new tickets From TRAC";
		else
			this.message = this.taskAdded + " new tickets downloaded";

		this.dialog.setMessage(this.message);
		this.dialog.setButton("Dismiss", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		this.dialog.show();
	}

	/**
	 * Method that shows a dialog and give the possibility both to retrieve
	 * (infinite times) tickets from trac and exit the activity
	 */
	private void createDialogProm() {
		dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Message");
		if (numberEvents == 0) {
			this.message = "No Events for PROM available";
		} else {
			message = numberEvents + " Events sent.";
			numberEvents = 0;
		}
		dialog.setMessage(message);
		dialog.setButton("Dismiss", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		dialog.show();
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
			TrackTicketFetcher tracTicketFetcher = new TrackTicketFetcher();
			ActivityFactory activityFactory = new ActivityFactory();
			this.tasks = tracTicketFetcher.fetch(super.user, super.dbHelper);
			this.taskAdded = activityFactory
					.produce(this.tasks, super.dbHelper);

		} catch (Exception e) {
			sendMessageHandler(Services.MESSAGE_EXCEPTION, e.toString());
			return;
		}
		Message message = new Message();
		message.what = Services.MESSAGE_OK;
		handler.sendMessage(message);
	}

	/**
	 * @throws PomodroidException
	 * 
	 *             This method takes all not-closed tickets from trac, then
	 *             inserts them into the local DB.
	 * 
	 */
	private void sendPromEvents() {
		try {
			if (this.zipIni == null || events == null) {
				progressDialog.dismiss();
				sendMessageHandler(Services.MESSAGE_INFORMATION, "No Events for PROM available.");
				return;
			}
			PromEventDeliverer promEventDeliverer = new PromEventDeliverer();
			if (promEventDeliverer.uploadData(this.zipIni, super.user)) {
				Event.deleteAll(super.dbHelper);
				events = null;
			}
		} catch (Exception e) {
			sendMessageHandler(Services.MESSAGE_EXCEPTION, e.toString());
			return;
		}
		Message message = new Message();
		message.what = Services.MESSAGE_OK;
		handler.sendMessage(message);
	}
	
	/**
	 * Sends a customized message to the Handler.
	 * @param messageType the type of message
	 * @param messageValue the text of the message
	 */
	private void sendMessageHandler(int messageType, String messageValue){
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("message", messageValue);
		message.setData(bundle);
		message.what = messageType;
		handler.sendMessage(message);
	}



}
