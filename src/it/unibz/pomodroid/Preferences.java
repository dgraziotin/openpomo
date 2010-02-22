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
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.XmlRpcClient;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * This class implements the user preferences. Here the user can set its user
 * and password for trac connections and the duration of one pomodoro. As soon
 * as the user clicks the button "save" some tests will check the correctness of
 * the data. The user can also manipulate the DB and launch the tests.
 * 
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * @see it.unibz.pomodroid.SharedActivity
 */

public class Preferences extends SharedActivity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.unibz.pomodroid.SharedActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);

		final CheckBox checkBoxAnonymous = (CheckBox) findViewById(R.id.CheckBoxAnonymous);
		final EditText tracUsername = (EditText) findViewById(R.id.EditTextUsername);
		final EditText tracPassword = (EditText) findViewById(R.id.EditTextPassword);
		
		if (super.user != null) {
			fillEditTexts(super.user);
			if (super.user.isTracAnonymousAccess()){
				checkBoxAnonymous.setChecked(true);
				tracPassword.setEnabled(false);
			}else{
				checkBoxAnonymous.setChecked(false);
			}
		} else {
			checkBoxAnonymous.setChecked(false);
		}
		
		Button saveButton = (Button) findViewById(R.id.ButtonSavePreferences);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (XmlRpcClient.isInternetAvailable(context)) {
					try {
						checkUserInput();
						testTracConnection();
						updateUser();
						throw new PomodroidException("Preferences saved.",
								"INFO");
					} catch (PomodroidException e) {
						e.alertUser(context);
					}
				} else {
					PomodroidException.createAlert(context, "ERROR", context
							.getString(R.string.no_internet_available));
				}
			}
		});
		
		checkBoxAnonymous.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				tracPassword.setEnabled(!isChecked);
			}
			
		}
         );
	}

	/**
	 * Tests if the given credentials and URL for Trac are correct
	 * 
	 * @throws PomodroidException
	 */
	private void testTracConnection() throws PomodroidException {
		final CheckBox checkBoxAnonymous = (CheckBox) findViewById(R.id.CheckBoxAnonymous);
		EditText tracUrl = (EditText) findViewById(R.id.EditTextTracUrl);
		EditText tracUsername = (EditText) findViewById(R.id.EditTextUsername);
		EditText tracPassword = (EditText) findViewById(R.id.EditTextPassword);
		Object[] params = {};
		Object[] result = {};
		
		// it should return an integer > 0
		if(checkBoxAnonymous.isChecked())
			result = XmlRpcClient.fetchMultiResults(tracUrl.getText()
					.toString(), "system.listMethods", params);
		else
			result = XmlRpcClient.fetchMultiResults(tracUrl.getText()
					.toString(), tracUsername.getText().toString(), tracPassword
					.getText().toString(), "system.listMethods", params);

		if (!(result.length > 0)) {
			throw new PomodroidException(
					"ERROR: something is wrong with Trac. Check username, password, URL and connectivity!");
		}
	}

	/**
	 * Tests if all the data is correctly filled by user
	 * 
	 * @throws PomodroidException
	 */
	private void checkUserInput() throws PomodroidException {
		EditText tracUrlEditText = (EditText) findViewById(R.id.EditTextTracUrl);
		EditText tracUsernameEditText = (EditText) findViewById(R.id.EditTextUsername);
		EditText tracPasswordEditText = (EditText) findViewById(R.id.EditTextPassword);
		EditText pomodoroLengthEditText = (EditText) findViewById(R.id.EditTextPomodoroLength);
		final CheckBox checkBoxAnonymous = (CheckBox) findViewById(R.id.CheckBoxAnonymous);
		if(checkBoxAnonymous.isChecked() && 
				nullOrEmpty(tracUrlEditText.getText().toString()) && 
				nullOrEmpty(tracUsernameEditText.getText().toString()))
			throw new PomodroidException("ERROR: you must fill al data!");
			
		if (!checkBoxAnonymous.isChecked() &&
				(	nullOrEmpty(tracUrlEditText.getText().toString()) || 
					nullOrEmpty(tracUsernameEditText.getText().toString()) || 
					nullOrEmpty(tracPasswordEditText.getText().toString()) ||
					nullOrEmpty(pomodoroLengthEditText.getText().toString())
				)
			)throw new PomodroidException("ERROR: you must fill al data!");
		if (Integer.parseInt(pomodoroLengthEditText.getText().toString()) == 0)
			throw new PomodroidException(
					"ERROR: Please set a correct Pomodoro length value (1-99)");
	}

	/**
	 * Updates all the EditText fields with already stored data
	 */
	private void fillEditTexts(User user) {
		EditText tracUrlEditText = (EditText) findViewById(R.id.EditTextTracUrl);
		EditText tracUsernameEditText = (EditText) findViewById(R.id.EditTextUsername);
		EditText tracPasswordEditText = (EditText) findViewById(R.id.EditTextPassword);
		final CheckBox checkBoxAnonymous = (CheckBox) findViewById(R.id.CheckBoxAnonymous);
		
		EditText pomodoroLengthEditText = (EditText) findViewById(R.id.EditTextPomodoroLength);
		tracUrlEditText.setText(user.getTracUrl());
		tracUsernameEditText.setText(user.getTracUsername());
		checkBoxAnonymous.setChecked(user.isTracAnonymousAccess());
		tracPasswordEditText.setText(user.getTracPassword());
		Integer pomodoroMinutesDuration = user.getPomodoroMinutesDuration();
		pomodoroLengthEditText.setText(pomodoroMinutesDuration.toString());
	}

	/**
	 * Updates or creates a User
	 */
	private void updateUser() throws PomodroidException {
		EditText tracUrlEditText = (EditText) findViewById(R.id.EditTextTracUrl);
		EditText tracUsernameEditText = (EditText) findViewById(R.id.EditTextUsername);
		EditText tracPasswordEditText = (EditText) findViewById(R.id.EditTextPassword);
		EditText pomodoroLengthEditText = (EditText) findViewById(R.id.EditTextPomodoroLength);
		final CheckBox checkBoxAnonymous = (CheckBox) findViewById(R.id.CheckBoxAnonymous);
		
		if (super.user == null) {
			super.user = new User(tracUsernameEditText.getText().toString(),
					tracPasswordEditText.getText().toString(), tracUrlEditText
							.getText().toString(), checkBoxAnonymous.isChecked());
			super.user.save(super.dbHelper);
		} else {
			super.user.setTracUsername(tracUsernameEditText.getText()
					.toString());
			super.user.setTracPassword(tracPasswordEditText.getText()
					.toString());
			super.user.setTracUrl(tracUrlEditText.getText().toString());
			super.user.setPomodoroMinutesDuration(Integer
					.parseInt(pomodoroLengthEditText.getText().toString()));
			super.user.setTracAnonymousAccess(checkBoxAnonymous.isChecked());
			super.user.save(super.dbHelper);
		}
	}

	/**
	 * Checks if a string is null or empty
	 * 
	 * @param string
	 *            the string to be checked
	 * @return true if the string is not null or not empty
	 */
	private boolean nullOrEmpty(String string) {
		return string.equals("") || string == null;
	}
}
