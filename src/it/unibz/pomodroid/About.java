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
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple Activity to show an about Window
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @see it.unibz.pomodroid.SharedActivity
 */
public class About extends SharedActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		TextView textViewAbout2 = (TextView) findViewById(R.id.TextViewAbout2);
		TextView textViewAbout4 = (TextView) findViewById(R.id.TextViewAbout4);
		Linkify.addLinks(textViewAbout2,Linkify.ALL);
		Linkify.addLinks(textViewAbout4,Linkify.ALL);
		Button goBack = (Button) findViewById(R.id.ButtonGoBack);
		goBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				if(!getUser().isAdvanced())
					intent.setClass(context, TodoTodaySheet.class);
				else
					intent.setClass(context, Pomodroid.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(intent);
				finish();
			}
		});	
	}
}
