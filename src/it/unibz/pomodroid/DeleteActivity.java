package it.unibz.pomodroid;

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
			//
		}
	}
	
}
