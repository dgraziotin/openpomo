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
import it.unibz.pomodroid.services.XmlRpcClient;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * This class implements the user preferences. Here the user can set its user
 * and password for trac connections and the duration of one pomodoro. As soon
 * as the user clicks the button "save" some tests will check the correctness of
 * the data. The user can also manipulate the DB and launch the tests.
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
		fillEditTexts(user);
		
		Button saveButton = (Button) findViewById(R.id.ButtonSavePreferences);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					try {
						checkUserInput();
						updateUser();
						throw new PomodroidException("Preferences saved.",
								"INFO");
					} catch (PomodroidException e) {
						e.alertUser(context);
					}
			}
		});
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
		advancedUserCheckBox.setChecked(super.user.isAdvancedUser());
	}

	/**
	 * Updates or creates a User
	 */
	private void updateUser() throws PomodroidException {
		EditText pomodoroLengthEditText = (EditText) findViewById(R.id.EditTextPomodoroLength);
		CheckBox advancedUserCheckBox = (CheckBox) findViewById(R.id.CheckBoxAdvancedUser);
			super.user.setPomodoroMinutesDuration(Integer
					.parseInt(pomodoroLengthEditText.getText().toString()));
			super.user.setAdvancedUser(advancedUserCheckBox.isChecked());
			super.user.save(super.dbHelper);
		Intent intent = new Intent();
		if(super.user.isAdvancedUser())
			intent.setClass(this, Pomodroid.class);
		else
			intent.setClass(this, TodoTodaySheet.class);
		startActivity(intent);

	}

	
}
