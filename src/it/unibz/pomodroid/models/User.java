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
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibz.pomodroid.models;

import java.util.Date;

/**
 * A class representing a tipical pomodroid user. Each user has its username (string), password (tring), 
 * trac url (absolute url)
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * 
 */

public class User {
	private int pomodoroMinutesDuration;
	private Date dateFacedPomodoro;
	private int facedPomodoro;
	
	/**
	 * @param tracUsername pomodroid username
	 * @param tracPassword pomodroid password
	 * @param tracUrl absolute trac url
	 */
	public User() {
		this.pomodoroMinutesDuration = 25;
		this.dateFacedPomodoro = new Date();
		this.facedPomodoro = 0;
	}
	
	/**
	 * @param tracUsername pomodroid username
	 * @param tracPassword pomodroid password
	 * @param tracUrl absolute trac url
	 */
	public User(int pomodoroMinutesDuration) {
		this.pomodoroMinutesDuration = pomodoroMinutesDuration;
		this.dateFacedPomodoro = new Date();
		this.facedPomodoro = 0;
	}

	/**
	 * This method updates some class variables 
	 * @param user
	 * 
	 */
	public void update (User user){
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
	 * @return date
	 * 
	 */
	public Date getDateFacedPomodoro() {
		return dateFacedPomodoro;
	}

	/**
	 * Set the date
	 * @param dateFacedPomodoro
	 */
	public void setDateFacedPomodoro(Date dateFacedPomodoro) {
		this.dateFacedPomodoro = dateFacedPomodoro;
	}

	/**
	 * This number is related to the date (methods above)
	 * @return faced pomodoro
	 *
	 */
	public int getFacedPomodoro() {
		return facedPomodoro;
	}

	/**
	 * The pomodoro faced to set
	 * @param facedPomodoro
	 * 
	 */
	public void setFacedPomodoro(int facedPomodoro) {
		this.facedPomodoro = facedPomodoro;
	}

	/**
	 * Every 4 pomodoro the user should do a longer break
	 * @return
	 * 
	 */
	public boolean isFourthPomodoro(){
		return (this.facedPomodoro % 4==0) ;
	}
	
	/**
	 * Add one pomodoro
	 */
	public void addPomodoro(){
		 this.facedPomodoro++;
	}
	
}
