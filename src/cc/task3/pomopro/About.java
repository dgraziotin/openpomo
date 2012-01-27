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
package cc.task3.pomopro;

import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * A simple Activity to show an About Window
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @see cc.task3.pomopro.SharedActivity
 */
public class About extends SharedActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        TextView atvAboutProjectURL = (TextView) findViewById(R.id.atvAboutProjectURL);
        TextView atvAboutGithubURL = (TextView) findViewById(R.id.atvAboutGithubURL);
        TextView atvAboutBugURL = (TextView) findViewById(R.id.atvAboutBugURL);
        TextView atvAboutEmailURL = (TextView) findViewById(R.id.atvAboutEmailURL);
        atvAboutEmailURL.setText(Html.fromHtml(getString(R.string.about_email_url)));
        
        Linkify.addLinks(atvAboutProjectURL, Linkify.ALL);
        Linkify.addLinks(atvAboutGithubURL, Linkify.ALL);
        Linkify.addLinks(atvAboutBugURL, Linkify.ALL);
        Linkify.addLinks(atvAboutEmailURL, Linkify.EMAIL_ADDRESSES);
    }
}
