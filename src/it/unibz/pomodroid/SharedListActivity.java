package it.unibz.pomodroid;

import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

public abstract class SharedListActivity extends ListActivity
{
	private static final int AIS = 0;
	private static final int TTS = 1;
	private static final int TS  = 2;
	private static final int PRE = 3;
	private static final int SET = 4;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, AIS, 0, "Activity Inventory Sheet").setIcon(android.R.drawable.ic_menu_upload);
		menu.add(0, TTS, 0, "Todo Today Sheet").setIcon(android.R.drawable.ic_menu_upload);
		menu.add(0, TS,  0, "Trash Sheet").setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, PRE, 0, "TRAC/PROM").setIcon(android.R.drawable.ic_menu_send);
		menu.add(0, SET, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
		return true;
	}
	
	public  boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
	    switch (item.getItemId()) {
	    case AIS: i = new Intent(this, ActivityInventorySheet.class);	
	    	startActivity(i);
	              return true;
	    case TTS: i = new Intent(this, TodoTodaySheet.class);
		          startActivity(i);
                  return true;
	    case TS:  i = new Intent(this, TrashSheet.class);
        		  startActivity(i);
        		  return true;
	    case PRE: i = new Intent(this, TracTicket.class);
		  		  startActivity(i);
		          return true;
	    case SET: i = new Intent(this, Preferences.class);
	    		  startActivity(i);
	    		  return true;
	    }
	    return false;
	}
   
}

