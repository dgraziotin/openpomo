package it.unibz.pomodroid;

import java.util.HashMap;
import java.util.Vector;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.TrackTicketFetcher;
import it.unibz.pomodroid.factories.ActivityFactory;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TracTicket extends Activity {
	
	private Vector<HashMap<String, Object>> tasks = null;
	private DBHelper dbHelper = null;
	private ProgressDialog progressDialog = null;
	private Runnable tasksRetriever = null;
	private TextView outputResult;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.traclist);
		this.dbHelper = new DBHelper(this);
		outputResult = (TextView) findViewById(R.id.tracResult);
		outputResult.setText("cacca");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		try {
			refreshSheet();
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		this.dbHelper.close();
	}

	@Override
	public void onStop() {
		super.onResume();
		this.dbHelper.close();
	}

	
	private void refreshSheet() throws PomodroidException {
		this.tasks = new Vector<HashMap<String,Object>>();
		this.tasksRetriever = new Runnable() {
			@Override
			public void run() {
				// retrieve the Activities from the database
				try {
					retrieveTicketsFromTrac();
				} catch (PomodroidException e) {
					// ugly but necessary
					try {
						throw new PomodroidException(e.getMessage());
					} catch (PomodroidException e1) {

					}
				}
			}
		};
		// create a new Thread that executes tasksRetriever and start it
		Thread thread = new Thread(null, tasksRetriever,"TasksRetrieverThread");
		thread.start();
		// show a nice progress bar
		progressDialog = ProgressDialog.show(TracTicket.this,"Please wait...", "Retrieving tickets from TRAC ...", true);

	}

	private void retrieveTicketsFromTrac() throws PomodroidException{
		int taskAdded = 0;
		try {
			User user = User.retrieve(dbHelper);
			tasks = TrackTicketFetcher.fetch(user, dbHelper);
			taskAdded = ActivityFactory.produce(tasks,dbHelper);
			Log.i("UFFAAA","uffa: " + taskAdded);
			showResult(taskAdded);
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}
	
	private synchronized void showResult(int taskRetrieved){
		// String a = " ticket/s from Trac have been added into the AIS!";
		progressDialog.dismiss();
		// if (taskRetrieved!=0)
		// outputResult.setText("fff");
	}

}
