package it.unibz.pomodroid;

import java.util.HashMap;
import java.util.Vector;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.services.TrackTicketFetcher;
import it.unibz.pomodroid.factories.ActivityFactory;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * @author Thomas Schievenin
 *
 */
public class TracTicket extends SharedActivity implements Runnable {
	
	private ProgressDialog progressDialog = null;
	private String message = "No new tickets From TRAC";
	private Vector<HashMap<String, Object>> tasks = null;
	private int taskAdded = 0;
	private AlertDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traclist);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		downloadData();
	}
	
	/**
	 * Method that starts a thread and shows a nice downloading bar.
	 */
	public void downloadData() {
		progressDialog = ProgressDialog.show(this, "Please wait", "Downloading tickets from TRAC", true, false);
	    Thread thread = new Thread(this);
	    thread.start();
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * 
	 * As soon as a thread starts, this method is called! It retrieves tickets from TRAC and when
	 * all tickets are downloaded, it sends an empty message to the handler in order to inform the
	 * system that the operation is finished.
	 */
	public void run() {
		try {
			retrieveTicketsFromTrac();
		} catch (PomodroidException e) {
			e.printStackTrace();
		}
		handler.sendEmptyMessage(0);
	}

	
	/**
	 * This handler waits until the method run() sends an empty message in order to inform us that
	 * the "retrieving phase" is finished.
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {	
			progressDialog.dismiss();
			createDialog();
		}
	};

	/**
	 * Method that shows a this.dialog and give the possibility both to retrieve (infinite times) tickets
	 * from trac and exit the activity
	 */
	private void createDialog(){
		this.dialog = new AlertDialog.Builder(TracTicket.this).create();
		this.dialog.setTitle("Message");
		if (this.taskAdded==0) {
		  this.dialog.setButton("Retry", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					onResume();
				}
		  });
		} else {
			this.message =  this.taskAdded + " new tickets downloaded";
		}
		this.dialog.setMessage(this.message);
		this.dialog.setButton2("Exit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				TracTicket.this.finish();
			}
		});
		this.dialog.show();
	}
	
	/**
	 * @throws PomodroidException
	 * 
	 * This method takes all not-closed tikets from track, then inserts them into the local DB.
	 * 
	 */
	private void retrieveTicketsFromTrac() throws PomodroidException{
		try {
			this.tasks = TrackTicketFetcher.fetch(super.user, super.dbHelper);
			this.taskAdded = ActivityFactory.produce(this.tasks,super.dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(super.context);
		}
	}

}
