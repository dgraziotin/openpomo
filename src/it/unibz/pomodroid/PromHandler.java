package it.unibz.pomodroid;

import java.util.List;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.services.PromEventDeliverer;
import it.unibz.pomodroid.factories.PromFactory;
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

public class PromHandler extends SharedActivity implements Runnable {
	private ProgressDialog progressDialog = null;
	private String message = "No Events to be sent to PROM are available";
	private int numberEvents = 0;
	private AlertDialog dialog;
	private byte[] zipIni = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traclist);
		List<Event> events = null;
		PromFactory promFactory = new PromFactory();
		try {
			events = Event.getAll(super.dbHelper);
			if(!(events==null || events.size()==0)){
				numberEvents = events.size();
				this.zipIni = promFactory.createZip(events, super.user);		
			}
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
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
		progressDialog = ProgressDialog.show(this, "Please wait", "Sending Data to PROM", true, false);
	    Thread thread = new Thread(this);
	    thread.start();
	}
	
	
	/**
	 * @see java.lang.Runnable#run()
	 * 
	 * As soon as a thread starts, this method is called! It retrieves tickets from TRAC and when
	 * all tickets are downloaded, it sends an empty message to the handler in order to inform the
	 * system that the operation is finished.
	 */
	public void run() {
		try {
			sendPromEvents();
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
		dialog = new AlertDialog.Builder(PromHandler.this).create();
		dialog.setTitle("Message");
		if (numberEvents==0) {
		  dialog.setButton("Retry", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int whichButton){
					onResume();
				}
		  });
		} else {
			message =  numberEvents + " Events sent.";
		}
		dialog.setMessage(message);
		dialog.setButton2("Exit", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				PromHandler.this.finish();
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
	private void sendPromEvents() throws PomodroidException{
		try {
			if(this.zipIni==null )
				return;
			PromEventDeliverer promEventDeliverer = new PromEventDeliverer();
			if(promEventDeliverer.uploadData(this.zipIni, super.user))
				Event.deleteAll(super.dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}

}