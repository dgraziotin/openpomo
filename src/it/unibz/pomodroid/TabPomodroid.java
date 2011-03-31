/**
 * 
 */
package it.unibz.pomodroid;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * @author bodom_lx
 *
 */
public class TabPomodroid extends TabActivity{
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    TabHost mTabHost;
	    mTabHost = getTabHost();
	    
	    mTabHost.addTab(mTabHost.newTabSpec("Activity Inventory").setContent(new Intent(this, ActivityInventorySheet.class)).
	    		 setIndicator("Inventory", getResources().getDrawable(android.R.drawable.ic_menu_agenda)));
	    mTabHost.addTab(mTabHost.newTabSpec("Todo Today").setContent(new Intent(this,TodoTodaySheet.class)).
	    		 setIndicator("Todo Today", getResources().getDrawable(android.R.drawable.ic_menu_day)));

	}
}
