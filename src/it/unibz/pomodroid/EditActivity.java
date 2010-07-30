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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * This class shows lets user to either create or edit an existing Service.
 * 
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @see it.unibz.pomodroid.SharedActivity
 */
public class EditActivity extends SharedActivity {
	/**
	 * This attribute holds the name of the Service that is
	 * passed through an Intent to this Activity
	 */
	private Integer originId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);

		try {
			this.originId = this.getIntent().getExtras().getInt(
					"originId");
			fillEmptyFields(this.originId);
		} catch (NullPointerException e) {
			this.originId = null;
		}
		

		Button saveButton = (Button) findViewById(R.id.ButtonSaveActivity);
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					checkUserInput();
					if(originId!=null){
						updateActivity();
						bringUserTo();
						throw new PomodroidException("Activity updated.",
						"INFO");
					}else{
						saveActivity();
						bringUserTo();
						throw new PomodroidException("Activity saved.",
						"INFO");
					}
					
					
				}catch(PomodroidException e){
					e.alertUser(context);
				}
			}
		});

	}

	/**
	 * This method is responsible for filling all the layout views if the user
	 * is editing an existing service
	 * @param serviceName the name of the Service, if any
	 */
	private void fillEmptyFields(Integer originId) {
		if (originId == null)
			return;
		Activity activity;
		try {
			activity = Activity.get("local", originId, dbHelper);
			EditText editTextSummary = (EditText) findViewById(R.id.EditTextSummary);
			editTextSummary.setText(activity.getSummary());
			EditText editTextDescription = (EditText) findViewById(R.id.EditTextDescription);
			editTextDescription.setText(activity.getDescription());
			DatePicker datePickerDeadline = (DatePicker) findViewById(R.id.DatePickerDeadline);
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(activity.getDeadline());
			
			datePickerDeadline.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}

	/**
	 * This method is responsible for updating an existing Service, after the user changes
	 * the related fields.
	 */
	private void updateActivity() throws PomodroidException {
		if (originId == null)
			return;
		Activity activity;
		try {
			activity = Activity.get("local", originId, dbHelper);
			EditText editTextSummary = (EditText) findViewById(R.id.EditTextSummary);
			activity.setSummary(editTextSummary.getText().toString());
			EditText editTextDescription = (EditText) findViewById(R.id.EditTextDescription);
			activity.setDescription(editTextDescription.getText().toString());
			DatePicker datePickerDeadline = (DatePicker) findViewById(R.id.DatePickerDeadline);
			Calendar calendar = new GregorianCalendar();
			calendar.set(datePickerDeadline.getYear(), datePickerDeadline.getMonth(), datePickerDeadline.getDayOfMonth());
			activity.setDeadline(calendar.getTime());
			if (!super.user.isAdvancedUser())
				activity.setTodoToday(true);
			activity.save(dbHelper);
		} catch (PomodroidException e) {
			e.alertUser(this);
		}
	}
	
	/**
	 * This method is responsible for saving a new Activity, after the user changes
	 * the related fields.
	 * @throws PomodroidException
	 */
	private void saveActivity() throws PomodroidException {
		EditText editTextSummary = (EditText) findViewById(R.id.EditTextSummary);
		EditText editTextDescription = (EditText) findViewById(R.id.EditTextDescription);
		DatePicker datePickerDeadline = (DatePicker) findViewById(R.id.DatePickerDeadline);
		Calendar calendar = new GregorianCalendar();
		calendar.set(datePickerDeadline.getYear(), datePickerDeadline.getMonth(), datePickerDeadline.getDayOfMonth());
		Activity activity = new Activity(0, new Date(), calendar.getTime(), editTextSummary.getText().toString(), editTextDescription.getText().toString(), 
				"local", Activity.getLastLocalId(dbHelper)+1, "medium", "you", "task");
		if (!super.user.isAdvancedUser())
			activity.setTodoToday(true);
		
		try{
			activity.save(dbHelper);
		}catch (PomodroidException e){
			e.alertUser(context);
		}

	}

	
	/**
	 * Tests if all the data is correctly filled by user
	 * 
	 * @throws PomodroidException
	 */
	private void checkUserInput() throws PomodroidException {
		EditText editTextSummary = (EditText) findViewById(R.id.EditTextSummary);
		if (nullOrEmpty(editTextSummary.getText().toString()))
			throw new PomodroidException(
					"Error. Please insert at least a Summary.");

	}
	
	private void bringUserTo(){
		Intent intent = new Intent();
		if (super.user.isAdvancedUser())
			intent.setClass(super.context, ActivityInventorySheet.class);
		else
			intent.setClass(super.context, TodoTodaySheet.class);
		startActivity(intent);
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
