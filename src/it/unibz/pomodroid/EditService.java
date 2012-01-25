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

import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import cc.task3.pomodroid.exceptions.PomodroidException;
import cc.task3.pomodroid.persistency.Service;
import cc.task3.pomodroid.services.XmlRpcClient;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * This class shows lets user to either create or edit an existing Service.
 * 
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @see cc.task3.pomodroid.SharedActivity
 */
public class EditService extends SharedActivity {
	/**
	 * This attribute holds the name of the Service that is passed through an
	 * Intent to this Activity
	 */
	private String serviceName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service);

		try {
			this.serviceName = this.getIntent().getExtras()
					.getString("serviceName");
		} catch (NullPointerException e) {
			this.serviceName = null;
		}

		CheckBox acbIsAnonymous = (CheckBox) findViewById(R.id.acbAnonymous);
		acbIsAnonymous
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						EditText aetPassword = (EditText) findViewById(R.id.aetPassword);
						aetPassword.setEnabled(!isChecked);
					}

				});

		fillEmptyFields(this.serviceName);

    }
        /**
         * We specify the menu labels and theirs icons
         *
         * @param menu
         * @return true
         */
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            menu.add(0, R.id.ACTION_SAVE, 0, "Save").setIcon(
                    android.R.drawable.ic_menu_save);
            return true;
        }

        /**
         * As soon as the user clicks on the menu a new intent is created for adding new Activity.
         *
         * @param item
         * @return
         */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            ScrollView scrollView = (ScrollView) findViewById(R.id.ScrollView01);
            switch (item.getItemId()) {
                case R.id.ACTION_SAVE:
                    if (XmlRpcClient.isInternetAvailable(context)) {
                        try {
                            checkUserInput();
                            testServiceConnection();
                            if (serviceName != null)
                                updateService();
                            else
                                saveService();
                            finish();
                            throw new PomodroidException("Service saved.",
                                    "SUCCESS");

                        } catch (PomodroidException e) {
                            e.alertUser(context);
                        }
                    } else {
                        PomodroidException.createAlert(context, "ERROR",
                                context.getString(R.string.no_internet_available));
                    }
                default:
                    return super.onOptionsItemSelected(item);
            }
        }


	/**
	 * This method is responsible for filling all the layout views if the user
	 * is editing an existing service
	 * 
	 * @param serviceName
	 *            the name of the Service, if any
	 */
	private void fillEmptyFields(String serviceName) {
		if (serviceName == null)
			return;
		Service service;
		try {
			service = Service.get(serviceName, super.getDbHelper());
			EditText aetName = (EditText) findViewById(R.id.aetName);
			aetName.setText(service.getName());
			EditText aetUrl = (EditText) findViewById(R.id.aetTracUrl);
			aetUrl.setText(service.getUrl());
			EditText aetUsername = (EditText) findViewById(R.id.aetUsername);
			aetUsername.setText(service.getUsername());
			EditText aetPassword = (EditText) findViewById(R.id.aetPassword);
			aetPassword.setText(service.getPassword());
			CheckBox acbIsAnonymous = (CheckBox) findViewById(R.id.acbAnonymous);
			acbIsAnonymous.setChecked(service.isAnonymousAccess());
			aetPassword.setEnabled(!service.isAnonymousAccess());
			CheckBox acbIsActive = (CheckBox) findViewById(R.id.acbIsActive);
			acbIsActive.setChecked(service.isActive());
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}

	/**
	 * This method is responsible for updating an existing Service, after the
	 * user changes the related fields.
	 */
	private void updateService() throws PomodroidException {

		EditText aetName = (EditText) findViewById(R.id.aetName);
		EditText aetUrl = (EditText) findViewById(R.id.aetTracUrl);

		Service currentService = Service.get(serviceName, super.getDbHelper());

		EditText aetUsername = (EditText) findViewById(R.id.aetUsername);
		EditText aetPassword = (EditText) findViewById(R.id.aetPassword);
		CheckBox acbIsAnonymous = (CheckBox) findViewById(R.id.acbAnonymous);
		CheckBox acbIsActive = (CheckBox) findViewById(R.id.acbIsActive);

		currentService.setName(aetName.getText().toString());
		currentService.setType("Trac");
		currentService.setUrl(aetUrl.getText().toString());
		currentService.setUsername(aetUsername.getText().toString());
		currentService.setPassword(aetPassword.getText().toString());
		currentService.setAnonymousAccess(acbIsAnonymous.isChecked());
		currentService.setActive(acbIsActive.isChecked());
		currentService.save(super.getDbHelper());

	}

	/**
	 * This method is responsible for saving a new Service, after the user
	 * changes the related fields.
	 * 
	 * @throws PomodroidException
	 */
	private void saveService() throws PomodroidException {
		EditText aetName = (EditText) findViewById(R.id.aetName);
		EditText aetUrl = (EditText) findViewById(R.id.aetTracUrl);

		Service service = new Service();

		EditText aetUsername = (EditText) findViewById(R.id.aetUsername);
		EditText aetPassword = (EditText) findViewById(R.id.aetPassword);
		CheckBox acbIsAnonymous = (CheckBox) findViewById(R.id.acbAnonymous);
		CheckBox acbIsActive = (CheckBox) findViewById(R.id.acbIsActive);

		service.setName(aetName.getText().toString());
		service.setType("Trac");
		service.setUrl(aetUrl.getText().toString());
		service.setUsername(aetUsername.getText().toString());
		service.setPassword(aetPassword.getText().toString());
		service.setAnonymousAccess(acbIsAnonymous.isChecked());
		service.setActive(acbIsActive.isChecked());
		service.save(super.getDbHelper());

	}

	/**
	 * Tests if the given credentials and URL for the Service are correct
	 * 
	 * @throws PomodroidException
	 */
	private void testServiceConnection() throws PomodroidException {
		EditText aetUrl = (EditText) findViewById(R.id.aetTracUrl);
		EditText aetUsername = (EditText) findViewById(R.id.aetUsername);
		EditText aetPassword = (EditText) findViewById(R.id.aetPassword);
		CheckBox acbIsAnonymous = (CheckBox) findViewById(R.id.acbAnonymous);

		Object[] params = {};
		Object[] result = {};

		// it should return an integer > 0
		if (acbIsAnonymous.isChecked())
			result = XmlRpcClient.fetchMultiResults(aetUrl.getText()
					.toString(), "system.listMethods", params);
		else
			result = XmlRpcClient.fetchMultiResults(aetUrl.getText()
					.toString(), aetUsername.getText().toString(),
					aetPassword.getText().toString(),
					"system.listMethods", params);

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
		EditText aetName = (EditText) findViewById(R.id.aetName);
		EditText aetUrl = (EditText) findViewById(R.id.aetTracUrl);
		EditText aetUsername = (EditText) findViewById(R.id.aetUsername);
		EditText aetPassword = (EditText) findViewById(R.id.aetPassword);
		CheckBox acbIsAnonymous = (CheckBox) findViewById(R.id.acbAnonymous);

		if (nullOrEmpty(aetName.getText().toString())
				|| nullOrEmpty(aetUrl.getText().toString())
				|| nullOrEmpty(aetUsername.getText().toString()))
			throw new PomodroidException(
					"Error. Please insert Name, URL and Username.");

		if (acbIsAnonymous.isChecked()
				&& nullOrEmpty(aetPassword.getText().toString()))
			throw new PomodroidException(
					"Error. Please insert a Password or use Anonymous Access.");

	}

}
