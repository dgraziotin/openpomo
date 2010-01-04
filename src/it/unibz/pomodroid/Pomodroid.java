package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Pomodroid extends Activity implements OnClickListener {
	
	private DBHelper dbHelper;
	private User user;
	private static final int AIS = 0;
	private static final int TTS = 1;
	private static final int TS  = 2;
	private static final int PRE = 3;
	private static final int SET = 4;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
<<<<<<< HEAD:src/it/unibz/pomodroid/Pomodroid.java
<<<<<<< HEAD:src/it/unibz/pomodroid/Pomodroid.java
<<<<<<< HEAD:src/it/unibz/pomodroid/Pomodroid.java
		dbHelper = new DBHelper(this);
		User user;
		
		dbHelper.deleteDatabase();
		
		
		try {
			u.save(dbHelper);
			a.save(dbHelper);a.save(dbHelper);a.save(dbHelper);b.save(dbHelper);b.save(dbHelper);
		} catch (PomodroidException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
=======
=======
		dbHelper = new DBHelper(this);
		TextView textView = (TextView) findViewById(R.id.hello);
		textView.setText("Pomodroid");
>>>>>>> 58d67ed... Bugfixes in persistency. First completed version of Prom events and properties:src/it/unibz/pomodroid/Pomodroid.java
		Button buttonAIS = (Button) findViewById(R.id.ButtonAIS);
		buttonAIS.setOnClickListener((OnClickListener) this);
		Button buttonTTS = (Button) findViewById(R.id.ButtonTTS);
		buttonTTS.setOnClickListener((OnClickListener) this);
		Button buttonTS = (Button) findViewById(R.id.ButtonTS);
		buttonTS.setOnClickListener((OnClickListener) this);
		Button buttonPreferences = (Button) findViewById(R.id.ButtonPreferences);
		buttonPreferences.setOnClickListener((OnClickListener) this);
		Button buttonTests = (Button) findViewById(R.id.ButtonTests);
		buttonTests.setOnClickListener((OnClickListener) this);
		Button buttonTRAC = (Button) findViewById(R.id.ButtonTrac);
		buttonTRAC.setOnClickListener((OnClickListener) this);
		
<<<<<<< HEAD:src/it/unibz/pomodroid/Pomodroid.java
<<<<<<< HEAD:src/it/unibz/pomodroid/Pomodroid.java
<<<<<<< HEAD:src/it/unibz/pomodroid/Pomodroid.java
		User user = User.retrieve(dbHelper);
<<<<<<< HEAD:src/it/unibz/pomodroid/Pomodroid.java

		textView.setText(user.getTracUsername());
=======
		Integer numActivities = it.unibz.pomodroid.persistency.Activity.getNumberActivities(dbHelper);
=======
		User user; 
		TrackTicketFetcher ttf = new TrackTicketFetcher();
	
		try{
			user = User.retrieve(dbHelper);;
			ttf.fetch(user, dbHelper);
		}catch(PomodroidException e){
			AlertDialog.Builder dialog = new AlertDialog.Builder(this); 
			   dialog.setTitle("MyException Occured");
			   dialog.setMessage(e.toString());
			   dialog.setNeutralButton("Ok", null);
			   dialog.create().show();
		}
>>>>>>> 99d0a51... PomodroidException expanded:src/it/unibz/pomodroid/Pomodroid.java
		
		textView.setText("");

		
>>>>>>> 525ca77... 100% method coverage at this point for tests:src/it/unibz/pomodroid/Pomodroid.java
		this.dbHelper.close();
=======
=======
>>>>>>> b977627... Activity Inventory Sheet improved:src/it/unibz/pomodroid/Pomodroid.java
=======
>>>>>>> 9fbd095... Bugfixes in persistency. First completed version of Prom events and properties:src/it/unibz/pomodroid/Pomodroid.java
>>>>>>> 58d67ed... Bugfixes in persistency. First completed version of Prom events and properties:src/it/unibz/pomodroid/Pomodroid.java
=======
>>>>>>> dbc2a17... First addition of Prom Events delivering in GUI:src/it/unibz/pomodroid/Pomodroid.java
		try {
			this.user = User.retrieve(dbHelper);
			if (user == null) {
				Intent intent = new Intent(this, Preferences.class);
				startActivity(intent);
			}
			/*
			PromEventDeliverer ped = new PromEventDeliverer();
			PromFactory pf = new PromFactory();
			List<Event> events = Event.getAll(dbHelper);
			
			byte[] zipIni = pf.createZip(events, user);
			if(ped.uploadData(zipIni, user))
				Event.deleteAll(dbHelper);
			textView.setText(zipIni.toString());
			*/
			
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
		super.onStop();
		this.dbHelper.close();
	}
	
	public void onResume(){
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		Intent i = null;
		switch (v.getId()) {
		case R.id.ButtonAIS:
			i = new Intent(this, ActivityInventorySheet.class);
			startActivity(i);
			break;
		case R.id.ButtonTTS:
			i = new Intent(this, TodoTodaySheet.class);
			startActivity(i);
			break;
		case R.id.ButtonTS:
			i = new Intent(this, TrashSheet.class);
			startActivity(i);
			break;
		case R.id.ButtonPreferences:
			i = new Intent(this, Preferences.class);
			startActivity(i);
			break;
		case R.id.ButtonTests:
			i = new Intent(this, PomodroidTest.class);
			startActivity(i);
			break;
		case R.id.ButtonTrac:
			i = new Intent(this, TracTicket.class);
			startActivity(i);
			break;
		case R.id.ButtonProm:
			i = new Intent(this, PromHandler.class);
			startActivity(i);
			break;

		}

>>>>>>> b8fec6e... first working alphas of AIS, TTS, TS, first mockup of launch Activity, centralized exception system:src/it/unibz/pomodroid/Pomodroid.java
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, AIS, 0, "Activity Inventory Sheet").setIcon(android.R.drawable.ic_menu_upload);
	    menu.add(0, TTS, 0, "Todo Today Sheet").setIcon(android.R.drawable.ic_menu_upload);
	    menu.add(0, TS,  0, "Trash Sheet").setIcon(android.R.drawable.ic_menu_delete);
	    menu.add(0, PRE, 0, "TRAC/PROM").setIcon(android.R.drawable.ic_menu_send);
	    menu.add(0, SET, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
	    switch (item.getItemId()) {
	    case AIS: i = new Intent(this, ActivityInventorySheet.class);
				  startActivity(i);
	              return true;
	    case TTS: i = new Intent(this, TodoTodaySheet.class);
		          startActivity(i);
                  return true;
	    case TS:  i = new Intent(this, TrashSheet.class);
        		  startActivity(i);
        		  return true;
	    case PRE: i = new Intent(this, TracTicket.class);
		  		  startActivity(i);
		          return true;
	    case SET: i = new Intent(this, Preferences.class);
	    		  startActivity(i);
	    		  return true;
	    }
	    return false;
	}

<<<<<<< HEAD:src/it/unibz/pomodroid/Pomodroid.java
}
=======

}
>>>>>>> b46b7f3... First version of a Pomodoro:src/it/unibz/pomodroid/Pomodroid.java
