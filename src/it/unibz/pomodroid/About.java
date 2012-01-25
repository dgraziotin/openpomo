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
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple Activity to show an About Window
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @see cc.task3.pomodroid.SharedActivity
 */
public class About extends SharedActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		TextView atvAbout2 = (TextView) findViewById(R.id.atvAbout2);
		TextView atvAbout4 = (TextView) findViewById(R.id.atvAbout4);
		Linkify.addLinks(atvAbout2,Linkify.ALL);
		Linkify.addLinks(atvAbout4,Linkify.ALL);
	}
}
