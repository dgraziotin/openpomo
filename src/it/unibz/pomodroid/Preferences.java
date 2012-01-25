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
package cc.task3.pomodroid;

import cc.task3.pomodroid.exceptions.PomodroidException;
import cc.task3.pomodroid.persistency.User;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This class implements the user preferences. As soon as the user clicks the
 * button "save" some tests will check the correctness of the data. The user can
 * also manipulate the DB and launch the tests.
 * 
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see cc.task3.pomodroid.SharedActivity
 */
public class Preferences extends SharedActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		TextView atvPreferences = (TextView) findViewById(R.id.atvPreferences);
		atvPreferences.setText(getString(R.string.preferences_intro));
		fillEditTexts(super.getUser());
	}

	/**
	 * Tests if all the data is correctly filled by user
	 * 
	 * @throws PomodroidException
	 */
	private void checkUserInput() throws PomodroidException {
		EditText aetPomodoroLength = (EditText) findViewById(R.id.aetPomodoroLength);
		if (aetPomodoroLength.getText().toString().equals(""))
			throw new PomodroidException(
					"ERROR: Please set a correct Pomodoro length value (1-99)");
		if (Integer.parseInt(aetPomodoroLength.getText().toString()) == 0)
			throw new PomodroidException(
					"ERROR: Please set a correct Pomodoro length value (1-99)");
	}

	/**
	 * Updates all the EditText fields with already stored data
	 */
	private void fillEditTexts(User user) {
		EditText aetPomodoroLength = (EditText) findViewById(R.id.aetPomodoroLength);
		Integer pomodoroMinutesDuration = user.getPomodoroMinutesDuration();
		aetPomodoroLength.setText(pomodoroMinutesDuration.toString());
		CheckBox acbAdvancedUser = (CheckBox) findViewById(R.id.acbAdvancedUser);
		acbAdvancedUser.setChecked(super.getUser().isAdvanced());
		CheckBox acbQuickActivityInsert = (CheckBox) findViewById(R.id.acbQuickActivityInsert);
		acbQuickActivityInsert.setChecked(super.getUser()
				.isQuickInsertActivity());
		CheckBox acbVibrate = (CheckBox) findViewById(R.id.acbVibrate);
		acbVibrate.setChecked(user.isVibration());
		CheckBox acbDimLight = (CheckBox) findViewById(R.id.acbDimLight);
		acbDimLight.setChecked(user.isDimLight());
	}

	/**
	 * Updates or creates a User
	 */
	private void updateUser() throws PomodroidException {
		EditText aetPomodoroLength = (EditText) findViewById(R.id.aetPomodoroLength);
		CheckBox acbAdvancedUser = (CheckBox) findViewById(R.id.acbAdvancedUser);
		CheckBox acbQuickActivityInsert = (CheckBox) findViewById(R.id.acbQuickActivityInsert);
		CheckBox acbVibrate = (CheckBox) findViewById(R.id.acbVibrate);
		CheckBox acbDimLight = (CheckBox) findViewById(R.id.acbDimLight);
		super.getUser().setPomodoroMinutesDuration(
				Integer.parseInt(aetPomodoroLength.getText().toString()));
		super.getUser().setAdvanced(acbAdvancedUser.isChecked());
		super.getUser().setQuickInsertActivity(
				acbQuickActivityInsert.isChecked());
		super.getUser().setVibration(acbVibrate.isChecked());
		super.getUser().setDimLight(acbDimLight.isChecked());
		super.getUser().save(super.getDbHelper());
		if (super.getUser().isAdvanced())
			startActivity(Pomodroid.class);
		else
			startActivity(TodoTodaySheet.class);

	}
	
	/**
	 * We specify the menu labels and theirs icons
	 * @param menu
	 * @return true 
	 *
	 */
	@Override
	public  boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, R.id.ACTION_SAVE, 0, "Save").setIcon( android.R.drawable.ic_menu_save);
		return true;
	}
	
	/**
	 * As soon as the user clicks on the menu a new intent is created for adding new Activity.
	 * @param item
	 * @return
	 * 
	 */
	@Override
	public  boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.ACTION_SAVE:
				try {
					checkUserInput();
					updateUser();
					throw new PomodroidException("Preferences saved.", "INFO");
				} catch (PomodroidException e) {
					e.alertUser(context);
				}
				return true;
		}
		return super.onOptionsItemSelected(item);
		
	}

}
