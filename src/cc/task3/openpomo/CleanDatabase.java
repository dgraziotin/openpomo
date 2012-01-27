/**
 * This file is part of OpenPomo.
 *
 *   OpenPomo is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   OpenPomo is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with OpenPomo.  If not, see <http://www.gnu.org/licenses/>.
 */
package cc.task3.openpomo;

import cc.task3.openpomo.exceptions.OpenPomoException;
import cc.task3.openpomo.models.*;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * This class allows us to manipulate the content of the object oriented database db4o.
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see cc.task3.openpomo.SharedActivity
 * @see android.view.View.OnClickListener
 * @see http://www.db4o.com
 */
public class CleanDatabase extends SharedActivity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cleandatabase);
        Button abDeleteActivitiesEvents = (Button) findViewById(R.id.abDeleteActivitiesEvents);
        abDeleteActivitiesEvents.setOnClickListener((OnClickListener) this);
        Button abDeleteDatabase = (Button) findViewById(R.id.abDeleteDatabase);
        abDeleteDatabase.setOnClickListener((OnClickListener) this);
        Button abDefragmentDatabase = (Button) findViewById(R.id.abDefragmentDatabase);
        abDefragmentDatabase.setOnClickListener((OnClickListener) this);
    }

    /**
     * Default listener for clicks.
     * Regarding to the button that has been clicked, the related action is
     * called.
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.abDeleteActivitiesEvents:
                    Activity.deleteAll(super.getDbHelper());
                    Event.deleteAll(super.getDbHelper());
                    throw new OpenPomoException(
                            "All activities and events deleted!", "INFO");
                case R.id.abDefragmentDatabase:
                    super.getDbHelper().defragment();
                    throw new OpenPomoException("Database defragmented.", "INFO");
                case R.id.abDeleteDatabase:
                    super.getDbHelper().deleteDatabase();
                    throw new OpenPomoException(
                            "Database destroyed. Please restart OpenPomo.", "INFO");
            }
        } catch (OpenPomoException e) {
            e.alertUser(context);
        } finally {
            super.getDbHelper().commit();
        }

    }
}
