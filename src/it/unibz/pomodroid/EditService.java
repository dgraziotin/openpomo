package it.unibz.pomodroid;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Service;
import it.unibz.pomodroid.services.XmlRpcClient;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EditService extends SharedActivity {
	private String serviceName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service);
		
		try{
			this.serviceName = this.getIntent().getExtras().getString("serviceName");
		}catch(NullPointerException e){
			this.serviceName = null;
		}
		
		CheckBox checkBoxIsAnonymous = (CheckBox) findViewById(R.id.CheckBoxAnonymous);
		checkBoxIsAnonymous.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				EditText editTextPassword = (EditText) findViewById(R.id.EditTextPassword);
				editTextPassword.setEnabled(!isChecked);
			}
			
		});
		
		fillEmptyFields(this.serviceName);
		/*
		Spinner spinnerServiceType = (Spinner) findViewById(R.id.spinnerServiceTypes);
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	            this, R.array.services, android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spinnerServiceType.setAdapter(adapter);
	    */

		
		Button saveButton = (Button) findViewById(R.id.ButtonSavePreferences);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (XmlRpcClient.isInternetAvailable(context)) {
					try {
						checkUserInput();
						testServiceConnection();
						updateService();
						throw new PomodroidException("Service saved.",
								"SUCCESS");
						
					} catch (PomodroidException e) {
						e.alertUser(context);
					}
				} else {
					PomodroidException.createAlert(context, "ERROR", context
							.getString(R.string.no_internet_available));
				}
			}
		});		
		
	}
	
	private void fillEmptyFields(String serviceName){
		if(serviceName==null)
			return;
		Service service;
		try {
			service = Service.get(serviceName, dbHelper);
			EditText editTextName = (EditText) findViewById(R.id.EditTextName);
			editTextName.setText(service.getName());
			EditText editTextUrl = (EditText) findViewById(R.id.EditTextTracUrl);
			editTextUrl.setText(service.getUrl());
			EditText editTextUsername = (EditText) findViewById(R.id.EditTextUsername);
			editTextUsername.setText(service.getUsername());
			EditText editTextPassword = (EditText) findViewById(R.id.EditTextPassword);
			editTextPassword.setText(service.getPassword());
			CheckBox checkBoxIsAnonymous = (CheckBox) findViewById(R.id.CheckBoxAnonymous);
			checkBoxIsAnonymous.setChecked(service.isAnonymousAccess());
			editTextPassword.setEnabled(!service.isAnonymousAccess());
			CheckBox checkBoxIsActive = (CheckBox) findViewById(R.id.CheckBoxIsActive);
			checkBoxIsActive.setChecked(service.isActive());
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}
	
	private void updateService() throws PomodroidException{
		EditText editTextName = (EditText) findViewById(R.id.EditTextName);
		Service service = Service.get(editTextName.getText().toString(), dbHelper);
		EditText editTextUrl = (EditText) findViewById(R.id.EditTextTracUrl);
		if(service==null){
			service = new Service();
			if(Service.isPresent(editTextName.getText().toString(), dbHelper))
				throw new PomodroidException("Error: you can not use the same name for two different Services.");
			if(Service.isPresentUrl(editTextUrl.getText().toString(), dbHelper))
				throw new PomodroidException("Error: you can not use the same URL for two different Services.");
		}
		
		
		EditText editTextUsername = (EditText) findViewById(R.id.EditTextUsername);
		EditText editTextPassword = (EditText) findViewById(R.id.EditTextPassword);
		CheckBox checkBoxIsAnonymous = (CheckBox) findViewById(R.id.CheckBoxAnonymous);
		CheckBox checkBoxIsActive = (CheckBox) findViewById(R.id.CheckBoxIsActive);
		
		service.setName(editTextName.getText().toString());
		service.setType("Trac");
		service.setUrl(editTextUrl.getText().toString());
		service.setUsername(editTextUsername.getText().toString());
		service.setPassword(editTextPassword.getText().toString());
		service.setAnonymousAccess(checkBoxIsAnonymous.isChecked());
		service.setActive(checkBoxIsActive.isChecked());
		service.save(dbHelper);
		

	}
	/**
	 * Tests if the given credentials and URL for Trac are correct
	 * 
	 * @throws PomodroidException
	 */
	private void testServiceConnection() throws PomodroidException {
		EditText editTextUrl = (EditText) findViewById(R.id.EditTextTracUrl);
		EditText editTextUsername = (EditText) findViewById(R.id.EditTextUsername);
		EditText editTextPassword = (EditText) findViewById(R.id.EditTextPassword);
		CheckBox checkBoxIsAnonymous = (CheckBox) findViewById(R.id.CheckBoxAnonymous);
		
		Object[] params = {};
		Object[] result = {};
		
		// it should return an integer > 0
		if(checkBoxIsAnonymous.isChecked())
			result = XmlRpcClient.fetchMultiResults(editTextUrl.getText()
					.toString(), "system.listMethods", params);
		else
			result = XmlRpcClient.fetchMultiResults(editTextUrl.getText()
					.toString(), editTextUsername.getText().toString(), editTextPassword
					.getText().toString(), "system.listMethods", params);

		if (!(result.length > 0)) {
			throw new PomodroidException(
					"ERROR: something is wrong with the Service. Check username, password, URL and connectivity!");
		}
	}
	

	/**
	 * Tests if all the data is correctly filled by user
	 * 
	 * @throws PomodroidException
	 */
	private void checkUserInput() throws PomodroidException {
		EditText editTextName = (EditText) findViewById(R.id.EditTextName);
		EditText editTextUrl = (EditText) findViewById(R.id.EditTextTracUrl);
		EditText editTextUsername = (EditText) findViewById(R.id.EditTextUsername);
		EditText editTextPassword = (EditText) findViewById(R.id.EditTextPassword);
		CheckBox checkBoxIsAnonymous = (CheckBox) findViewById(R.id.CheckBoxAnonymous);
		
		if (
				nullOrEmpty(editTextName.getText().toString()) ||
				nullOrEmpty(editTextUrl.getText().toString()) ||
				nullOrEmpty(editTextUsername.getText().toString())
		) throw new PomodroidException("Error. Please insert Name, URL and Username.");
		
		if(checkBoxIsAnonymous.isChecked() && nullOrEmpty(editTextPassword.getText().toString()))
			throw new PomodroidException("Error. Please insert a Password or use Anonymous Access.");
			
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
