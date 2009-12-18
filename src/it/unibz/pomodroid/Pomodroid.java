package it.unibz.pomodroid;

import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.TrackTicketFetcher;
import android.app.Activity;
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
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		dbHelper = new DBHelper(this);
		this.button = (Button) findViewById(R.id.Button01);
		TextView textView = (TextView) findViewById(R.id.TextView01);
		
		this.button.setOnClickListener(new OnClickListener() {
			  @Override
			  public void onClick(View v) {
			    finish();
			  }
			});
		
		User user = User.retrieve(dbHelper);
		TrackTicketFetcher.fetch(user, dbHelper);

		textView.setText("ciao");

		
		this.dbHelper.close();
	}
	

}