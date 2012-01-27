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

import android.os.Bundle;
import android.widget.TextView;
import cc.task3.pomotrac.exceptions.PomodroidException;
import cc.task3.pomotrac.models.*;


/**
 * A simple Activity to show an About Window
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @see cc.task3.pomotrac.SharedActivity
 */
public class Statistics extends SharedActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);
        DBHelper dbHelper = getDbHelper();

        String activitiesCreated = "0";
        String activitiesCompleted = "0";
        String pomodoroStarted = "0";
        String pomodoroFinished = "0";
        String pomodoroBroken = "0";
        String pomodoroFinishedStarted = "0";

        TextView atvActivitiesCreated = (TextView) findViewById(R.id.atvActivitiesCreated);
        TextView atvActivitiesCompleted = (TextView) findViewById(R.id.atvActivitiesCompleted);
        TextView atvPomodoroStarted = (TextView) findViewById(R.id.atvPomodoroStarted);
        TextView atvPomodoroFinished = (TextView) findViewById(R.id.atvPomodoroFinished);
        TextView atvPomodoroBroken = (TextView) findViewById(R.id.atvPomodoroBroken);
        TextView atvPomodoroFinishedStarted = (TextView) findViewById(R.id.atvPomodorosFinishedStarted);

        try {
            activitiesCreated = new Integer(Activity.getAll(dbHelper).size()).toString();
            activitiesCompleted = new Integer(Activity.getCompleted(dbHelper).size()).toString();
            Integer numPomodoroStarted = new Integer(Event.getAllStartedPomodoros(dbHelper).size());
            pomodoroStarted = numPomodoroStarted.toString();
            Integer numPomodoroFinished = new Integer(Event.getAllFinishedPomodoros(dbHelper).size());
            pomodoroFinished = numPomodoroFinished.toString();
            pomodoroBroken = new Integer(Event.getAllInterruptions(dbHelper).size()).toString();
            Integer numPomodoroFinishedStarted = (int) (((float) numPomodoroFinished / (float) numPomodoroStarted) * 100);
            pomodoroFinishedStarted = numPomodoroFinishedStarted.toString().concat("%");
        } catch (PomodroidException e) {
            // TODO Auto-generated catch block
            e.alertUser(getContext());
        }


        atvActivitiesCreated.setText(activitiesCreated);
        atvActivitiesCompleted.setText(activitiesCompleted);
        atvPomodoroStarted.setText(pomodoroStarted);
        atvPomodoroFinished.setText(pomodoroFinished);
        atvPomodoroBroken.setText(pomodoroBroken);
        atvPomodoroFinishedStarted.setText(pomodoroFinishedStarted);


    }
}
