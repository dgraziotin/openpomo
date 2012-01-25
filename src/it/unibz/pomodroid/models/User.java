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
package cc.task3.pomodroid.models;

import java.util.Date;

/**
 * A class representing a tipical pomodroid user. Each user has its username (string), password (tring),
 * trac url (absolute url)
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 */

public class User {
    private int pomodoroMinutesDuration;
    private Date dateFacedPomodoro;
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
        this.advanced = false;
        this.quickInsertActivity = false;
        this.vibration = false;
        this.dimLight = false;
    }


    public User(int pomodoroMinutesDuration) {
        this.pomodoroMinutesDuration = pomodoroMinutesDuration;
        this.dateFacedPomodoro = new Date();
        this.facedPomodoro = 0;
        this.advanced = false;
        this.quickInsertActivity = false;
        this.vibration = false;
        this.dimLight = false;
        this.displayDonations = true;
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

}
