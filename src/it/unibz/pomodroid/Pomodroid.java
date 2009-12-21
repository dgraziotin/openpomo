package it.unibz.pomodroid;

import java.util.Date;

import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.Event;
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
		
		User user = User.retrieve(dbHelper);
<<<<<<< HEAD:src/it/unibz/pomodroid/Pomodroid.java

		textView.setText(user.getTracUsername());
=======
		Integer numActivities = it.unibz.pomodroid.persistency.Activity.getNumberActivities(dbHelper);
		
		textView.setText("");

		
>>>>>>> 525ca77... 100% method coverage at this point for tests:src/it/unibz/pomodroid/Pomodroid.java
		this.dbHelper.close();
	}
	

}
