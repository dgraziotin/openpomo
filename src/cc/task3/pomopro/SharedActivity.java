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

import cc.task3.pomopro.exceptions.PomodroidException;
import cc.task3.pomopro.models.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Base-class of all activities. Defines common behavior for all Activities of
 * Pomodroid.
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @see android.app.Activity
 */
public abstract class SharedActivity extends Activity {
    /**
     * The Database container for db4o
     */
    private DBHelper dbHelper;
    /**
     * The current user
     */
    private User user;
    /**
     * The Context of the Activity
     */
    protected Context context;

    /**
     * @return dbHelper object
     */
    public DBHelper getDbHelper() {
        return dbHelper;
    }

    /**
     * Set dbHelper
     *
     * @param dbHelper
     */
    public void setDbHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    /**
     * @return user object
     */
    public User getUser() {
        if (user == null) {
            try {
                if (User.isPresent(dbHelper)) {
                    this.setUser(User.retrieve(dbHelper));
                } else {
                    this.user = new User();
                    this.user.setPomodoroMinutesDuration(25);
                    this.user.save(this.dbHelper);
                }
            } catch (PomodroidException e) {
                e.alertUser(this);
            }
        } else {
            this.refreshUser();
        }

        return user;
    }

    public void refreshUser() {
        try {
            setUser(User.retrieve(dbHelper));
        } catch (PomodroidException e) {
            e.alertUser(context);
        }
    }


    /**
     * User to set
     *
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }

    protected Context getContext() {
        return this.context;
    }

    /**
     * Coding standards order:
     * 1 - onCreate()
     * 2 - onStart()
     * 4 - onRestart()
     * 5 - onResume()
     * 6 - onPause()
     * 7 - onStop()
     * 8 - onDestroy()
     */

    /**
     * Every sub-class of this one automatically receive an instance of the User
     * and of the Database Container. This method is also responsible of
     * bringing the User to the Preferences the first time it starts Pomodroid
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dbHelper = new DBHelper(getApplicationContext());
        this.context = this;
        this.user = this.getUser();
    }

    /**
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * @see android.app.Activity#onRestart()
     */
    @Override
    public void onRestart() {
        super.onRestart();
    }

    /**
     * @see android.app.Activity#onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * Every time an Activity looses focus, it is forced to commit changes to
     * the Database
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        this.dbHelper.commit();
    }

    /**
     * @see android.app.Activity#onStop()
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Wrapper for starting Android Activities and adding
     * Intents and Flags
     *
     * @param klass                 - the class to be started
     * @param finishCurrentActivity - true if calling activity must terminate
     * @param recycleActivity       - true if the called activity can be retrieved from stack if present
     */

    protected void startActivity(Class<?> klass) {
        Intent intent = new Intent(this.context, klass);
        startActivity(intent);
    }


    /**
     * Checks if a string is null or empty
     *
     * @param string the string to be checked
     * @return true if the string is not null or not empty
     */
    public static boolean nullOrEmpty(String string) {
        return string.equals("") || string == null;
    }
}
