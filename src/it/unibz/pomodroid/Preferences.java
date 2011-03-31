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
import it.unibz.pomodroid.persistency.User;
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
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedActivity
 */
public class Preferences extends SharedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		TextView textViewPreferences = (TextView) findViewById(R.id.TextViewPreferences);
		textViewPreferences.setText(getString(R.string.preferences_intro));
		fillEditTexts(super.getUser());

		
	}
	

	/**
	 * Tests if all the data is correctly filled by user
	 * 
	 * @throws PomodroidException
	 */
	private void checkUserInput() throws PomodroidException {
		EditText pomodoroLengthEditText = (EditText) findViewById(R.id.EditTextPomodoroLength);
		if (pomodoroLengthEditText.getText().toString().equals(""))
			throw new PomodroidException(
					"ERROR: Please set a correct Pomodoro length value (1-99)");
		if (Integer.parseInt(pomodoroLengthEditText.getText().toString()) == 0)
			throw new PomodroidException(
					"ERROR: Please set a correct Pomodoro length value (1-99)");
	}

	/**
	 * Updates all the EditText fields with already stored data
	 */
	private void fillEditTexts(User user) {
		EditText pomodoroLengthEditText = (EditText) findViewById(R.id.EditTextPomodoroLength);
		Integer pomodoroMinutesDuration = user.getPomodoroMinutesDuration();
		pomodoroLengthEditText.setText(pomodoroMinutesDuration.toString());
		CheckBox advancedUserCheckBox = (CheckBox) findViewById(R.id.CheckBoxAdvancedUser);
		advancedUserCheckBox.setChecked(super.getUser().isAdvanced());
		CheckBox quickActivityInsertCheckBox = (CheckBox) findViewById(R.id.CheckBoxQuickActivityInsert);
		quickActivityInsertCheckBox.setChecked(super.getUser()
				.isQuickInsertActivity());
		CheckBox checkBoxVibrate = (CheckBox) findViewById(R.id.CheckBoxVibrate);
		checkBoxVibrate.setChecked(user.isVibration());
		CheckBox checkBoxDimLight = (CheckBox) findViewById(R.id.CheckBoxDimLight);
		checkBoxDimLight.setChecked(user.isDimLight());
		CheckBox checkBoxNotifications = (CheckBox) findViewById(R.id.CheckBoxNotifications);
		checkBoxNotifications.setChecked(user.isNotifications());
	}

	/**
	 * Updates or creates a User
	 */
	private void updateUser() throws PomodroidException {
		EditText pomodoroLengthEditText = (EditText) findViewById(R.id.EditTextPomodoroLength);
		CheckBox advancedUserCheckBox = (CheckBox) findViewById(R.id.CheckBoxAdvancedUser);
		CheckBox quickInsertActivityCheckBox = (CheckBox) findViewById(R.id.CheckBoxQuickActivityInsert);
		CheckBox checkBoxVibrate = (CheckBox) findViewById(R.id.CheckBoxVibrate);
		CheckBox checkBoxDimLight = (CheckBox) findViewById(R.id.CheckBoxDimLight);
		CheckBox checkBoxNotifications = (CheckBox) findViewById(R.id.CheckBoxNotifications);
		super.getUser().setPomodoroMinutesDuration(
				Integer.parseInt(pomodoroLengthEditText.getText().toString()));
		super.getUser().setAdvanced(advancedUserCheckBox.isChecked());
		super.getUser().setQuickInsertActivity(
				quickInsertActivityCheckBox.isChecked());
		super.getUser().setVibration(checkBoxVibrate.isChecked());
		super.getUser().setDimLight(checkBoxDimLight.isChecked());
		super.getUser().setNotifications(checkBoxNotifications.isChecked());
		super.getUser().save(super.getDbHelper());
		if (super.getUser().isAdvanced())
			startActivity(Pomodroid.class, true, true);
		else
			startActivity(TodoTodaySheet.class, true, true);

	}
	
	/**
	 * We specify the menu labels and theirs icons
	 * @param menu
	 * @return true 
	 *
	 */
	@Override
	public  boolean onCreateOptionsMenu(Menu menu) {
			menu.add(0, ACTION_SAVE, 0, "Save Preferences").setIcon(
					android.R.drawable.ic_menu_save);
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
		case ACTION_SAVE:
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
