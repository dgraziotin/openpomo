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
package it.unibz.pomodroid.models;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A class representing an activity. Each activity is described by its number of pomodoro (integer number),
 * received date (date), deadline (date), summary (string), description (string), origin (string i.e. trac), 
 * origin id (integer that refers to its origin), priority (string), reporter (string), type (string), 
 * todoToday (boolean), done (boolean) and endDate (date).
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * 
 */

public class Activity {
	
	/**
	 * Number of Pomodoros dedicated to the Activity
	 */
	private int numberPomodoro;
	/**
	 * Date of Activity creation
	 */
	private Date received;
	/**
	 * Deadline of an Activity
	 */
	private Date deadline;
	/**
	 * Short summary or title of the Activity
	 */
	private String summary;
	/**
	 * Description of the Activity
	 */
	private String description;
	/**
	 * String that describes the origin of the Activity (name of Service, local)
	 */
	private String origin;
	/**
	 * Original id of the Activity as Issue in the Service
	 */
	private int originId;
	/**
	 * Priority of the issue
	 */
	private String priority;
	/**
	 * Name of the User reporting the Issue
	 */
	private String reporter;
	/**
	 * Type of Issue (bug, feature etc.)
	 */
	private String type;
	/**
	 * Represents if the Activity is in the TTS
	 */
	private boolean todoToday;
	/**
	 * True when the Activity is set as finished
	 */
	private boolean done;
	/**
	 * Stores the Date of Activity finished
	 */
	private Date doneDate;

	/**
	 * @param number_pomodoro number of pomodoro that has been runned
	 * @param received received date 
	 * @param deadline deadline
	 * @param summary a briefly activity description 
	 * @param description a more detalied description
	 * @param origin describes the activity origin
	 * @param origin_id id referring to the origin, it is impossible to have two activities with same origin and origin id 
	 * @param priority priority of the activity
	 * @param reporter name of the reporter
	 * @param type activity type
	 */
	public Activity(int numberPomodoro, Date received,
			Date deadline, String summary, String description, String origin,
			int originId, String priority, String reporter, String type) {
		this.numberPomodoro = numberPomodoro;
		this.received = received;
		this.deadline = deadline;
		this.summary = summary;
		this.description = description;
		this.origin = origin;
		this.originId = originId;
		this.priority = priority;
		this.reporter = reporter;
		this.type = type;
		this.todoToday = false;
		this.done = false;
	}
	/**
	 * Creates a quite empty Activity
	 * @param origin
	 * @param originId
	 */
	public Activity (String origin, int originId) {
		this.origin = origin;
		this.originId = originId;
	}
	
	/**
	 * A helper method to update an existing Activity
	 * @param ac
	 */
	public void update (Activity ac) {
		this.numberPomodoro = ac.getNumberPomodoro();
		this.summary = ac.getSummary();
		this.todoToday = ac.isTodoToday();
		this.done = ac.isDone();
	}
	/**
	 * @return the number of pomodoro
	 */
	public int getNumberPomodoro() {
		return numberPomodoro;
	}

	/**
	 * sets the number of pomodoro
	 * @param numberPomodoro
	 * 
	 */
	public void setNumberPomodoro(int numberPomodoro) {
		this.numberPomodoro = numberPomodoro;
	}

	/**
	 * @return when the activity has been received
	 */
	public Date getReceived() {
		return received;
	}

	/**
	 * received date to set
	 * @param received
	 * 
	 */
	public void setReceived(Date received) {
		this.received = received;
	}

	/**
	 * @return the deadline
	 */
	public Date getDeadline() {
		return deadline;
	}

	/**
	 * @return the deadline as a string
	 */
	public String getStringDeadline() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(this.deadline).toString();
	}
	/**
	 * deadline to set
	 * @param deadline
	 * 
	 */
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * description to set
	 * @param description
	 * 
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * origin to set
	 * @param origin
	 * 
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * @return the origin id
	 */
	public int getOriginId() {
		return originId;
	}

	/**
	 * origin_id to set
	 * @param originId
	 * 
	 */
	public void setOriginId(int originId) {
		this.originId = originId;
	}

	/**
	 * @return the reporter name
	 */
	public String getReporter() {
		return reporter;
	}

	/**
	 * reporter name to set
	 * @param reporter
	 * 
	 */
	public void setReporter(String reporter) {
		this.reporter = reporter;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * the type to set
	 * @param type
	 * 
	 */
	public void setType(String type) {
		this.type = type;
	}

	
	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	
	/**
	 * summary to set
	 * @param summary
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	
	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}

	
	/**
	 * the priority to set
	 * @param priority
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}

	/**
	 * @return todo today
	 */
	public boolean isTodoToday() {
		return todoToday;
	}

	/**
	 * the todoToday to set
	 * @param todoToday
	 * 
	 */
	public void setTodoToday(boolean todoToday) {
		this.todoToday = todoToday;
	}

	/**
	 * @return is done
	 */
	public boolean isDone() {
		return done;
	}

	/**
	 * Set the activity as DONE and store the date
	 */
	public void setDone() {
		this.done = true;
		setEndDate(new Date());
	}

	/**
	 * @return end date
	 */
	public Date getEndDate() {
		return doneDate;
	}

	/**
	 * the date to set
	 * @param date
	 * 
	 */
	public void setEndDate(Date endDate) {
		this.doneDate = endDate;
	}
	
	/**
	 *  Set done to false
	 */
	public void setUndone() {
		this.done = false;
	}

	/**
	 * @return short summary description (for UI)
	 */
	public String getShortSummary(){
	   final int maxLength = 30;
	   if (this.summary.length()>maxLength)
		 return summary.substring(0, maxLength).replaceAll("\n", " ")+"...";
	   return this.summary.replaceAll("\n", " ");
	}
	
	/**
	 * Add one pomodoro
	 */
	public void addOnePomodoro (){
		this.numberPomodoro++;
	}
}