package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Event;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DeleteActivity extends SharedActivity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deleteactivity);
		Button buttonTRAC = (Button) findViewById(R.id.ButtonDeleteActivity);
		buttonTRAC.setOnClickListener((OnClickListener) this);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ButtonDeleteActivity) {
			try {
				it.unibz.pomodroid.persistency.Activity.deleteAll(dbHelper);
				Event.deleteAll(dbHelper);
				throw new PomodroidException("All activities and events deleted!","INFO");
			} catch (PomodroidException e) {
				e.alertUser(context);
			}finally{
				dbHelper.close();
			}
		}
	}
	
}
