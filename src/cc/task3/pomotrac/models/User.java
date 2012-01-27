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
package cc.task3.pomotrac.models;

import android.util.Log;
import com.db4o.ObjectSet;
import cc.task3.pomotrac.exceptions.PomodroidException;

import java.util.Date;

/**
 * A class representing a tipical pomotrac user. Each user has its username (string), password (tring),
 * trac url (absolute url)
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 */

public class User {
    private int pomodoroMinutesDuration;
    private Date dateFacedPomodoro;
    private Activity selectedActivity;
    private boolean underPomodoro;
    private int facedPomodoro;
    private boolean advanced;
    private boolean quickInsertActivity;
    private boolean vibration;
    private boolean dimLight;
    private boolean displayDonations;


    public User() {
        this.pomodoroMinutesDuration = 25;
        this.dateFacedPomodoro = new Date();
        this.facedPomodoro = 0;
        this.selectedActivity = null;
        this.underPomodoro = false;
        this.advanced = false;
        this.quickInsertActivity = false;
        this.vibration = false;
        this.dimLight = false;

    }


    public User(int pomodoroMinutesDuration) {
        this.pomodoroMinutesDuration = pomodoroMinutesDuration;
        this.dateFacedPomodoro = new Date();
        this.facedPomodoro = 0;
        this.selectedActivity = null;
        this.underPomodoro = false;
        this.advanced = false;
        this.quickInsertActivity = false;
        this.vibration = false;
        this.dimLight = false;
        this.displayDonations = true;
    }

    public boolean isUnderPomodoro() {
        return underPomodoro;
    }

    public void setUnderPomodoro(boolean runningActivity) {
        underPomodoro = runningActivity;
    }

    public Activity getSelectedActivity() {
        return selectedActivity;
    }

    public void setSelectedActivity(Activity selectedActivity) {
        this.selectedActivity = selectedActivity;
    }


    /**
     * This method updates some class variables
     *
     * @param user
     */
    public void update(User user) {
        this.pomodoroMinutesDuration = user.getPomodoroMinutesDuration();
    }


    /**
     * @return pomodoroMinutesDuration length of a Pomodoro in minutes
     */
    public int getPomodoroMinutesDuration() {
        return pomodoroMinutesDuration;
    }

    /**
     * @param pomodoroMinutesDuration length of a Pomodoro in minutes
     */
    public void setPomodoroMinutesDuration(int pomodoroMinutesDuration) {
        this.pomodoroMinutesDuration = pomodoroMinutesDuration;
    }

    /**
     * this date is related to the number of pomodoro faced.
     *
     * @return date
     */
    public Date getDateFacedPomodoro() {
        return dateFacedPomodoro;
    }

    /**
     * Set the date
     *
     * @param dateFacedPomodoro
     */
    public void setDateFacedPomodoro(Date dateFacedPomodoro) {
        this.dateFacedPomodoro = dateFacedPomodoro;
    }

    /**
     * This number is related to the date (methods above)
     *
     * @return faced pomodoro
     */
    public int getFacedPomodoro() {
        return facedPomodoro;
    }

    /**
     * The pomodoro faced to set
     *
     * @param facedPomodoro
     */
    public void setFacedPomodoro(int facedPomodoro) {
        this.facedPomodoro = facedPomodoro;
    }

    /**
     * @return the advanced
     */
    public boolean isAdvanced() {
        return advanced;
    }

    /**
     * @param advanced the advanced to set
     */
    public void setAdvanced(boolean advanced) {
        this.advanced = advanced;
    }

    /**
     * @return the quickInsertActivity
     */
    public boolean isQuickInsertActivity() {
        return quickInsertActivity;
    }

    /**
     * @param quickInsertActivity the quickInsertActivity to set
     */
    public void setQuickInsertActivity(boolean quickInsertActivity) {
        this.quickInsertActivity = quickInsertActivity;
    }

    /**
     * @return the vibration
     */
    public boolean isVibration() {
        return vibration;
    }

    /**
     * @param vibration the vibration to set
     */
    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    /**
     * @return the dimLight
     */
    public boolean isDimLight() {
        return dimLight;
    }

    /**
     * @param dimLight the dimLight to set
     */
    public void setDimLight(boolean dimLight) {
        this.dimLight = dimLight;
    }


    /**
     * Every 4 pomodoro the user should do a longer break
     *
     * @return
     */
    public boolean isFourthPomodoro() {
        return (this.facedPomodoro % 4 == 0);
    }

    /**
     * Add one pomodoro
     */
    public void addPomodoro() {
        this.facedPomodoro++;
    }

    public boolean isDisplayDonations() {
        return displayDonations;
    }

    public void setDisplayDonations(boolean displayDonations) {
        this.displayDonations = displayDonations;
    }

    /**
     * @param username
     * @param dbHelper
     * @return
     * @throws cc.task3.pomotrac.exceptions.PomodroidException
     *
     */
    public static boolean isPresent(DBHelper dbHelper) throws PomodroidException {
        User user;
        try {
            user = User.retrieve(dbHelper);
            if (user == null)
                return false;
            else
                return true;
        } catch (Exception e) {
            Log.e("User.isPresent()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in User.isPresent():" + e.toString());
        }
    }

    /**
     * Save an user
     *
     * @param dbHelper
     * @throws PomodroidException
     */
    public void save(DBHelper dbHelper) throws PomodroidException {
        if (!isPresent(dbHelper)) {
            try {
                dbHelper.getDatabase().store(this);
                Log.i("User.save()", "User Saved.");
            } catch (Exception e) {
                Log.e("User.save()", "Problem: " + e.toString());
                throw new PomodroidException("ERROR in User.save()" + e.toString());
            }
        } else {
            try {
                User updateUser = retrieve(dbHelper);
                updateUser.update(this);
                dbHelper.getDatabase().store(updateUser);
            } catch (Exception e) {
                Log.e("User.save()", "Update Problem: " + e.toString());
                throw new PomodroidException("ERROR in User.save(update)" + e.toString());
            } finally {
                dbHelper.commit();
            }
        }
    }

    /**
     * Returns the first user in the db
     *
     * @param dbHelper
     * @return an object of type user
     * @throws PomodroidException
     */
    public static User retrieve(DBHelper dbHelper) throws PomodroidException {
        ObjectSet<User> users;
        try {
            users = dbHelper.getDatabase().queryByExample(User.class);
            if (users == null || users.isEmpty()) {
                return null;
            } else {
                return users.next();
            }
        } catch (Exception e) {
            Log.e("User.retrieve()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in User.retrieve()" + e.toString());
        }

    }

    /**
     * Check if the date saved into the db is equal to the current one.
     *
     * @param userDate
     * @return
     */
    public boolean isSameDay(Date userDate) {
        Date today = new Date();
        return (today.getDay() == userDate.getDay() &&
                today.getMonth() == userDate.getMonth() &&
                today.getYear() == userDate.getYear());
    }

    /**
     * Checks whether the user should do a short or long break
     *
     * @param dbHelper
     * @return
     * @throws PomodroidException
     */
    public boolean isLongerBreak(DBHelper dbHelper) throws PomodroidException {
        User user = User.retrieve(dbHelper);
        if (isSameDay(user.getDateFacedPomodoro())) {
            user.addPomodoro();
            user.save(dbHelper);
            return user.isFourthPomodoro();
        } else {
            user.setFacedPomodoro(1);
            user.setDateFacedPomodoro(new Date());
            user.save(dbHelper);
            return false;
        }
    }

}
