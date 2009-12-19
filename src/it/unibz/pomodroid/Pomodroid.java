package it.unibz.pomodroid;

import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.TrackTicketFetcher;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Pomodroid extends Activity {
	private DBHelper dbHelper;
	private Button button;
	private TextView textView;
	private Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		dbHelper = new DBHelper(this);
		this.context = this;
		this.button = (Button) findViewById(R.id.Button01);
		this.textView = (TextView) findViewById(R.id.TextView01);
		
		this.button.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View v) {
			    finish();
			    Intent intent = new Intent(context, PomodroidTest.class);
			    context.startActivity(intent);
			  }
		});
		
		User user = new User("dgraziotin","rootl1nUx","https://babbage.inf.unibz.it/trac/AIT0910-projectpomodroid/xmlrpc","http://task3.cc:8080"); //User.retrieve(dbHelper);
		user.save(dbHelper);
		TrackTicketFetcher.fetch(user, dbHelper);

		textView.setText("ciao");

		
		this.dbHelper.close();
	}
	

}