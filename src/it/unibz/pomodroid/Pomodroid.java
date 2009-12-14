package it.unibz.pomodroid;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import it.unibz.pomodroid.*;
import it.unibz.pomodroid.factories.PromFactory;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.PromEventDeliverer;
import it.unibz.pomodroid.services.XmlRpcClient;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Pomodroid extends Activity {
	private DBHelper dbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHelper = new DBHelper(this);
		TextView tv = new TextView(this);
		User user = User.retrieve(dbHelper);

		
		
		List<Event> events = Event.getAll(dbHelper);
		
		PromFactory pf = new PromFactory();
		PromEventDeliverer pe = new PromEventDeliverer();
		try {
			pe.uploadData(pf.createZip(events, user), user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e("blah",e.toString());
		}
		
		tv.setText("Arrivato alla fine\nRPCVersion: ");
        setContentView(tv);
        
		// setContentView(R.layout.main);
		this.dbHelper.close();
	}

}