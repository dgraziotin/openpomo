package it.unibz.pomodroid;

import java.util.HashMap;
import java.util.Vector;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.TrackTicketFetcher;
import it.unibz.pomodroid.factories.ActivityFactory;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;



/**
 * @author Thomas Schievenin
 *
 */
public class TracTicket extends SharedActivity implements Runnable {
	
	private Vector<HashMap<String, Object>> tasks = null;
	private DBHelper dbHelper = null;
	private ProgressDialog progressDialog = null;
	private String message = "No new tickets From TRAC";
	private int taskAdded = 0;
	private AlertDialog dialog;
	private Context context = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traclist);
		this.dbHelper = new DBHelper(this);
		this.context = this;
	}
	
	@Override
	public void onResume() {
		downloadData();
		super.onResume();
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
	 * Method that shows a dialog and give the possibility both to retrieve (infinite times) tickets
	 * from trac and exit the activity
	 */
	private void createDialog(){
		dialog = new AlertDialog.Builder(TracTicket.this).create();
		dialog.setTitle("Message");
		if (taskAdded==0) {
		  dialog.setButton("Retry", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					onResume();
				}
		  });
		} else {
			message =  taskAdded + " new tickets downloaded";
		}
		dialog.setMessage(message);
		dialog.setButton2("Exit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				TracTicket.this.finish();
			}
		});
		dialog.show();
	}
	
	/**
	 * @throws PomodroidException
	 * 
	 * This method takes all not-closed tikets from track, then inserts them into the local DB.
	 * 
	 */
	private void retrieveTicketsFromTrac() throws PomodroidException{
		try {
			User user = User.retrieve(dbHelper);
			tasks = TrackTicketFetcher.fetch(user, dbHelper);
			taskAdded = ActivityFactory.produce(tasks,dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(context);
		}
	}

}
