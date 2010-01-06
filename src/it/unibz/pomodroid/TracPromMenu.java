package it.unibz.pomodroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TracPromMenu extends SharedActivity implements OnClickListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracprom);
		Button buttonTRAC = (Button) findViewById(R.id.ButtonTrac);
		buttonTRAC.setOnClickListener((OnClickListener) this);
		Button buttonProm = (Button) findViewById(R.id.ButtonProm);
		buttonProm.setOnClickListener((OnClickListener) this);
	}
	
	@Override
	public void onClick(View v) {
		Intent i = null;
		if (v.getId() == R.id.ButtonTrac) {
			i = new Intent(this, TracTicket.class);
			startActivity(i);
		}
		else {
			i = new Intent(this, PromHandler.class);
			startActivity(i);
		}
	}

}
