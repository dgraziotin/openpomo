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
package cc.task3.pomotrac;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import cc.task3.pomotrac.exceptions.PomodroidException;
import cc.task3.pomotrac.models.*;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;

/**
 * This class shows lets user to either create or edit an existing Activity.
 * It only lets users to edit local activities, not those remotely retrieved
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @see cc.task3.pomotrac.SharedActivity
 */
public class EditActivity extends SharedActivity {
    /**
     * This member holds the unique id of the Activity
     * if we are editing it
     */
    private Integer originId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        try {
            this.originId = this.getIntent().getExtras().getInt("originId");
            fillEmptyFields(this.originId);
        } catch (NullPointerException e) {
            this.originId = null;
        }

    }

    /**
     * This method is responsible for filling all the layout views if the user
     * is editing an existing activity
     *
     * @param originId the unique id of the Activity
     */
    private void fillEmptyFields(Integer originId) {
        if (originId == null)
            return;
        Activity activity;
        try {
            activity = Activity.get("local", originId, super.getDbHelper());
            EditText aetSummary = (EditText) findViewById(R.id.aetSummary);
            aetSummary.setText(activity.getSummary());
            EditText aetDescription = (EditText) findViewById(R.id.aetDescription);
            aetDescription.setText(activity.getDescription());
            DatePicker adpDeadline = (DatePicker) findViewById(R.id.adpDeadline);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(activity.getDeadline());

            adpDeadline.updateDate(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
        } catch (PomodroidException e) {
            e.alertUser(this);
        }
    }

    /**
     * This method is responsible for updating an existing Activity, after the
     * user changes the related fields.
     */
    private void updateActivity() throws PomodroidException {
        if (originId == null)
            return;
        Activity activity;
        try {
            activity = Activity.get("local", originId, super.getDbHelper());
            EditText aetSummary = (EditText) findViewById(R.id.aetSummary);
            activity.setSummary(aetSummary.getText().toString());
            EditText aetDescription = (EditText) findViewById(R.id.aetDescription);
            activity.setDescription(aetDescription.getText().toString());
            DatePicker adpDeadline = (DatePicker) findViewById(R.id.adpDeadline);
            Calendar calendar = new GregorianCalendar();
            calendar.set(adpDeadline.getYear(),
                    adpDeadline.getMonth(),
                    adpDeadline.getDayOfMonth());
            activity.setDeadline(calendar.getTime());
            if (!super.getUser().isAdvanced())
                activity.setTodoToday(true);
            activity.save(super.getDbHelper());
        } catch (PomodroidException e) {
            e.alertUser(this);
        }
    }

    /**
     * This method is responsible for saving a new Activity, after the user
     * changes the related fields.
     *
     * @throws PomodroidException
     */
    private void saveActivity() throws PomodroidException {
        EditText aetSummary = (EditText) findViewById(R.id.aetSummary);
        EditText aetDescription = (EditText) findViewById(R.id.aetDescription);
        DatePicker adpDeadline = (DatePicker) findViewById(R.id.adpDeadline);
        Calendar calendar = new GregorianCalendar();
        calendar.set(adpDeadline.getYear(),
                adpDeadline.getMonth(),
                adpDeadline.getDayOfMonth());
        Activity activity = new Activity(0, 0, new Date(), calendar.getTime(),
                aetSummary.getText().toString(), aetDescription
                .getText().toString(), "local",
                Activity.getLastLocalId(super.getDbHelper()) + 1, "medium", "you", "task");
        if (!super.getUser().isAdvanced())
            activity.setTodoToday(true);

        try {
            activity.save(super.getDbHelper());
        } catch (PomodroidException e) {
            e.alertUser(context);
        }

    }

    /**
     * Tests if all the data is correctly filled by user
     *
     * @throws PomodroidException
     */
    private void checkUserInput() throws PomodroidException {
        EditText aetSummary = (EditText) findViewById(R.id.aetSummary);
        if (nullOrEmpty(aetSummary.getText().toString()))
            throw new PomodroidException(
                    "Error. Please insert at least a Summary.");
    }

    private void bringUserTo() {
        if (super.getUser().isAdvanced())
            startActivity(TabPomodroid.class);
        else
            startActivity(TodoTodaySheet.class);

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
        menu.add(0, R.id.ACTION_CLEAR, 0, "Cancel").setIcon(
                android.R.drawable.ic_menu_close_clear_cancel);
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
        switch (item.getItemId()) {
            case R.id.ACTION_SAVE:
                try {
                    checkUserInput();
                    if (originId != null) {
                        updateActivity();
                        bringUserTo();
                        throw new PomodroidException("Activity updated.",
                                "INFO");
                    } else {
                        saveActivity();
                        bringUserTo();
                        throw new PomodroidException("Activity saved.", "INFO");
                    }

                } catch (PomodroidException e) {
                    e.alertUser(context);
                }
                return true;
            case R.id.ACTION_CLEAR:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);

    }


}
