package it.unibz.pomodroid;

import java.net.URI;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import it.unibz.pomodroid.*;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
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
		
		// User tom = new User("","","");
		// tom.save(dbHelper);
		
		URI uri = URI.create("https://babbage.inf.unibz.it/trac/AIT0910-projectpomodroid/xmlrpc/");
		
		XMLRPCClient client = new XMLRPCClient(uri); 
        //client.setBasicAuthentication("tschievenin", "");
        int sum=666;
        
        try {
			sum = (Integer) client.call("wiki.getRPCVersionSupported");
		} catch (XMLRPCException e) {
			Log.e("ERRORE","MERDINA " + e.toString());
		}
        
		tv.setText("Arrivato alla fine\nRPCVersion: " + sum);
        setContentView(tv);
		// setContentView(R.layout.main);
		this.dbHelper.close();
	}

}