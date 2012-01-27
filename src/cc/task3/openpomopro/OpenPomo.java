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
package cc.task3.openpomopro;

import android.os.Bundle;
import cc.task3.openpomopro.exceptions.OpenPomoException;

/**
 * Main activity. It just loads the layout.
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see cc.task3.openpomopro.SharedActivity
 */
public class OpenPomo extends SharedActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.getUser().setSelectedActivity(null);
        super.getUser().setUnderPomodoro(false);
        try {
            super.getUser().save(getDbHelper());
        } catch (OpenPomoException e) {
            e.alertUser(getContext());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //TODO: notification for openpomopro donation
        if (!super.getUser().isAdvanced())
            startActivity(TodoTodaySheet.class);
        else
            startActivity(TabOpenPomo.class);
    }
}
