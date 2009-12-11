package it.unibz.pomodroid;

import java.net.URI;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

import it.unibz.pomodroid.*;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
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
		
		Object[] result = XmlRpcClient.fetchMultiResults(user.getTracUrl(),user.getTracUsername(),user.getTracPassword(), "wiki.getAllPages",null);
        
		tv.setText("Arrivato alla fine\nRPCVersion: " + result[1].toString());
        setContentView(tv);
		// setContentView(R.layout.main);
		this.dbHelper.close();
	}

}