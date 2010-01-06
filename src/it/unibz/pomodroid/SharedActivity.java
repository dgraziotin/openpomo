package it.unibz.pomodroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import it.unibz.pomodroid.SharedMenu;


public abstract class SharedActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedMenu.setContext(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        SharedMenu.onCreateOptionsMenu(menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return SharedMenu.onOptionsItemSelected(item);
	}
}
