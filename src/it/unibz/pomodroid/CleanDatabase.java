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

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Event;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This class allows us to manipulate the content of the object oriented database db4o.
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedActivity
 * @see android.view.View.OnClickListener
 */
public class CleanDatabase extends SharedActivity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cleandatabase);
		Button buttonDeleteActivitiesEvents = (Button) findViewById(R.id.ButtonDeleteActivitiesEvents);
		buttonDeleteActivitiesEvents.setOnClickListener((OnClickListener) this);
		Button buttonDeleteDatabase = (Button) findViewById(R.id.ButtonDeleteDatabase);
		buttonDeleteDatabase.setOnClickListener((OnClickListener) this);
		Button buttonDefragmentDatabase = (Button) findViewById(R.id.ButtonDefragmentDatabase);
		buttonDefragmentDatabase.setOnClickListener((OnClickListener) this);
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
		try {
			switch (v.getId()) {
			case R.id.ButtonDeleteActivitiesEvents:
				it.unibz.pomodroid.persistency.Activity.deleteAll(dbHelper);
				Event.deleteAll(dbHelper);
				throw new PomodroidException(
						"All activities and events deleted!", "INFO");
			case R.id.ButtonDefragmentDatabase:
				dbHelper.defragment();
				throw new PomodroidException("Database defragmented.", "INFO");
			case R.id.ButtonDeleteDatabase:
				dbHelper.deleteDatabase();
				throw new PomodroidException(
						"Database destroyed. Please restart Pomodroid.", "INFO");
			}
		} catch (PomodroidException e) {
			e.alertUser(context);
		} finally {
			dbHelper.commit();
		}

	}
}
