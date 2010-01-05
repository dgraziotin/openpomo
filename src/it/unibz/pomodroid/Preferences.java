package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.PromEventDeliverer;
import it.unibz.pomodroid.services.XmlRpcClient;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Preferences extends SharedActivity {
	private DBHelper dbHelper;
	private Context context;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.preferences);
		dbHelper = new DBHelper(this);
		this.context = this;
		try {
			User user = User.retrieve(dbHelper);
			if (user != null)
				fillEditTexts(user);
		} catch (PomodroidException e1) {
			// TODO Auto-generated catchblock
			e1.printStackTrace();
		}

		Button saveButton = (Button) findViewById(R.id.ButtonSavePreferences);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					checkUserInput();
					testTracConnection();
					testPromConnection();
					updateUser();
					throw new PomodroidException("Preferences saved.","INFO");
				} catch (PomodroidException e) {
					e.alertUser(context);
				}
			}
		});
		Button deleteActivitiesButton = (Button) findViewById(R.id.ButtonDeleteActivies);
		deleteActivitiesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					it.unibz.pomodroid.persistency.Activity.deleteAll(dbHelper);
					Event.deleteAll(dbHelper);
					throw new PomodroidException("All activities and events deleted!","INFO");
				} catch (PomodroidException e) {
					e.alertUser(context);
				}finally{
					dbHelper.close();
				}
			}
		});

	}

	@Override
	public void onPause() {
		super.onPause();
		this.dbHelper.close();
	}

	public void onStop() {
		super.onResume();
		this.dbHelper.close();
	}

	/**
	 * Tests if the given credentials and URL for Trac are correct
	 * 
	 * @throws PomodroidException
	 */
	private void testTracConnection() throws PomodroidException {
		EditText tracUrl = (EditText) findViewById(R.id.EditTextTracUrl);
		EditText tracUsername = (EditText) findViewById(R.id.EditTextUsername);
		EditText tracPassword = (EditText) findViewById(R.id.EditTextPassword);
		Object[] params = {};
		// it should return an integer > 0
		Object[] result = XmlRpcClient.fetchMultiResults(tracUrl.getText()
				.toString(), tracUsername.getText().toString(), tracPassword
				.getText().toString(), "system.listMethods", params);
		if (!(result.length > 0)) {
			throw new PomodroidException("ERROR: something is wrong with Trac. Check username, password, URL and connectivity!");
		}
	}

	/**
	 * Tests if the given credentials and URL for PROM are correct
	 * 
	 * @throws PomodroidException
	 */
	private void testPromConnection() throws PomodroidException {
		PromEventDeliverer ped = new PromEventDeliverer();
		EditText tracUrlEditText = (EditText) findViewById(R.id.EditTextTracUrl);
		EditText tracUsernameEditText = (EditText) findViewById(R.id.EditTextUsername);
		EditText tracPasswordEditText = (EditText) findViewById(R.id.EditTextPassword);
		EditText promUrlEditText = (EditText) findViewById(R.id.EditTextPromUrl);

		User user = new User(tracUsernameEditText.getText().toString(),
				tracPasswordEditText.getText().toString(), tracUrlEditText
						.getText().toString(), promUrlEditText.getText()
						.toString());
		int id = ped.getUploadId(user);
		if (!(id > 0)) {
			throw new PomodroidException("ERROR: something is wrong with PROM. Check URL and connectivity!");
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
		EditText promUrlEditText = (EditText) findViewById(R.id.EditTextPromUrl);
		EditText pomodoroLengthEditText = (EditText) findViewById(R.id.EditTextPomodoroLength);
		if (nullOrEmpty(tracUrlEditText.getText().toString())
				|| nullOrEmpty(tracUsernameEditText.getText().toString())
				|| nullOrEmpty(tracPasswordEditText.getText().toString())
				|| nullOrEmpty(promUrlEditText.getText().toString())
				|| nullOrEmpty(pomodoroLengthEditText.getText().toString()) )
			throw new PomodroidException("ERROR: you must fill al data!");
	}

	/**
	 * Updates all the EditText fields with already stored data
	 */
	private void fillEditTexts(User user) {
		EditText tracUrlEditText = (EditText) findViewById(R.id.EditTextTracUrl);
		EditText tracUsernameEditText = (EditText) findViewById(R.id.EditTextUsername);
		EditText tracPasswordEditText = (EditText) findViewById(R.id.EditTextPassword);
		EditText promUrlEditText = (EditText) findViewById(R.id.EditTextPromUrl);
		EditText pomodoroLengthEditText = (EditText) findViewById(R.id.EditTextPomodoroLength);
		tracUrlEditText.setText(user.getTracUrl());
		tracUsernameEditText.setText(user.getTracUsername());
		tracPasswordEditText.setText(user.getTracPassword());
		promUrlEditText.setText(user.getPromUrl());
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
		EditText promUrlEditText = (EditText) findViewById(R.id.EditTextPromUrl);
		EditText pomodoroLengthEditText = (EditText) findViewById(R.id.EditTextPomodoroLength);

		User user = User.retrieve(dbHelper);

		if (user == null) {
			user = new User(tracUsernameEditText.getText().toString(),
					tracPasswordEditText.getText().toString(), tracUrlEditText
							.getText().toString(), promUrlEditText.getText()
							.toString());
			user.save(dbHelper);
		} else {
			user.setTracUsername(tracUsernameEditText.getText().toString());
			user.setTracPassword(tracPasswordEditText.getText().toString());
			user.setTracUrl(tracUrlEditText.getText().toString());
			user.setPromUrl(promUrlEditText.getText().toString());
			user.setPomodoroMinutesDuration(Integer.parseInt(pomodoroLengthEditText.getText().toString()));
			user.save(dbHelper);

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
