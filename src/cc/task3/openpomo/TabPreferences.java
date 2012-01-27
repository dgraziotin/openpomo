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

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

/**
 * This class implements the tabview visible within the users preferences
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * @see android.app.TabActivity
 */
public class TabPreferences extends TabActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TabHost mTabHost;
        mTabHost = getTabHost();

        mTabHost.addTab(mTabHost.newTabSpec("pref").setContent(new Intent(TabPreferences.this, Preferences.class)).
                setIndicator(this.getString(R.string.preferences), getResources().getDrawable(android.R.drawable.ic_menu_preferences)));
        mTabHost.addTab(mTabHost.newTabSpec("db4o").setContent(new Intent(TabPreferences.this, CleanDatabase.class)).
                setIndicator(this.getString(R.string.db4o), getResources().getDrawable(android.R.drawable.ic_menu_edit)));


    }
}
