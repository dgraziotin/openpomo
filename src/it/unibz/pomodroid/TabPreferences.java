package it.unibz.pomodroid;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class TabPreferences extends TabActivity{
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    TabHost mTabHost;
	    mTabHost = getTabHost();
	    
	    mTabHost.addTab(mTabHost.newTabSpec("pref").setContent(new Intent(TabPreferences.this, Preferences.class)).
	    		 setIndicator(this.getString(R.string.preferences), getResources().getDrawable(android.R.drawable.ic_menu_preferences)));
	    mTabHost.addTab(mTabHost.newTabSpec("db4o").setContent(new Intent(TabPreferences.this, DeleteActivity.class)).
	    		 setIndicator(this.getString(R.string.db4o), getResources().getDrawable(android.R.drawable.ic_menu_edit)));
	    mTabHost.addTab(mTabHost.newTabSpec("test").setContent(new Intent(TabPreferences.this,PomodroidTest.class)).
	    		 setIndicator(this.getString(R.string.tests), getResources().getDrawable(android.R.drawable.ic_menu_directions)));

	}
}
