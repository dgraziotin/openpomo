/**
 * This file is part of Pomodroid.
 *
 *   Pomodroid is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Pomodroid is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with Pomodroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibz.pomodroid;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Main activity. It just loads the layout.
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedActivity
 */
public class Pomodroid extends SharedActivity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(!super.user.isAdvancedUser()){
			Intent intent = new Intent(this, TodoTodaySheet.class);
			startActivity(intent);
		}
		setContentView(R.layout.pomodroidadvanced);
		Button buttonAIS = (Button) findViewById(R.id.ButtonAIS);
		buttonAIS.setOnClickListener(this);
		Button buttonTTS = (Button) findViewById(R.id.ButtonTTS);
		buttonTTS.setOnClickListener(this);
		Button buttonTS = (Button) findViewById(R.id.ButtonTS);
		buttonTS.setOnClickListener(this);
		Button buttonServices = (Button) findViewById(R.id.ButtonServices);
		buttonServices.setOnClickListener(this);
		Button buttonPreferences = (Button) findViewById(R.id.ButtonPreferences);
		buttonPreferences.setOnClickListener(this);
		Button buttonAbout = (Button) findViewById(R.id.ButtonAbout);
		buttonAbout.setOnClickListener(this);	
	}
	
	/**
	 * Default listener for clicks.
	 * Regarding to the button that has been clicked, the related action is
	 * called.
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * 
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch(v.getId()){
			case R.id.ButtonAIS:
				intent.setClass(this, ActivityInventorySheet.class);
				break;
			case R.id.ButtonTTS:
				intent.setClass(this, TodoTodaySheet.class);
				break;
			case R.id.ButtonTS:
				intent.setClass(this, TrashSheet.class);
				break;
			case R.id.ButtonServices:
				intent.setClass(this, Services.class);
				break;
			case R.id.ButtonPreferences:
				intent.setClass(this, TabPreferences.class);
				break;
			case R.id.ButtonAbout:
				intent.setClass(this, About.class);
				break;
		}
		startActivity(intent);
		
	}
}
