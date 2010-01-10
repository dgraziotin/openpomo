package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.factories.ActivityFactory;
import it.unibz.pomodroid.factories.PromFactory;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.services.PromEventDeliverer;
import it.unibz.pomodroid.services.TrackTicketFetcher;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Services extends SharedActivity implements OnClickListener,
		Runnable {

	private ProgressDialog progressDialog = null;
	private int numberEvents = 0;
	private AlertDialog dialog;
	private byte[] zipIni = null;
	private Vector<HashMap<String, Object>> tasks = null;
	private int taskAdded = 0;
	private String message = null;

	private static final int PROM = 1;
	private static final int TRAC = 2;
	private static int source = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.services);
		List<Event> events = null;
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
	public void onClick(View v) {
		if (v.getId() == R.id.ButtonTrac) {
			source = TRAC;
		} else {
			source = PROM;
		}
		useServices();
	}

	/**
	 * Method that starts a thread and shows a nice downloading bar.
	 */
	public void useServices() {
		String message = null;
		if (source == PROM)
			message = "Sending Data to PROM";
		else
			message = "Downloading tickets from TRAC";

		progressDialog = ProgressDialog.show(this, "Please wait", message,
				true, false);
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * @see java.lang.Runnable#run()
	 * As soon as a thread starts, this method is called.
	 * If source == PROM, it sends the events to PROM
	 * If source == TRAC, it retrieves tickets from TRAC.
	 * When the operation is finished,it sends an empty message to the handler
	 * in order to inform the system that the operation is
	 * finished.
	 */
	public void run() {
		try {
			if (source == PROM)
				sendPromEvents();
			else
				retrieveTicketsFromTrac();
		} catch (PomodroidException e) {
			e.printStackTrace();
		}
		handler.sendEmptyMessage(0);
	}

	/**
	 * This handler waits until the method run() sends an empty message in order
	 * to inform us that the "retrieving phase" is finished.
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			if (source == PROM)
				createDialogProm();
			else
				createDialogTrac();
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
	 *             This method takes all not-closed tikets from track, then
	 *             inserts them into the local DB.
	 * 
	 */
	private void retrieveTicketsFromTrac() throws PomodroidException {
		try {
			this.tasks = TrackTicketFetcher.fetch(super.user, super.dbHelper);
			this.taskAdded = ActivityFactory
					.produce(this.tasks, super.dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(super.context);
		}
	}

	/**
	 * @throws PomodroidException
	 * 
	 * This method takes all not-closed tickets from trac, then
	 * inserts them into the local DB.
	 * 
	 */
	private void sendPromEvents() throws PomodroidException {
		try {
			if (this.zipIni == null)
				return;
			PromEventDeliverer promEventDeliverer = new PromEventDeliverer();
			if (promEventDeliverer.uploadData(this.zipIni, super.user))
				Event.deleteAll(super.dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}

}
