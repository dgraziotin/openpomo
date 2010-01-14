package it.unibz.pomodroid.models;

import java.util.Date;

/**
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * 
 * A class representing an event. Each event has its type (string), value (string), date (date) and reference to
 * its activity.
 *
 */
public class Event {

	private String type;
	private String value;
	private Date timestamp;
	private Activity activity;
	private long atPomodoroValue;

	
	/**
	 * @param type event type
	 * @param value event value
	 * @param timestamp date
	 * @param activity reference to its activity
	 */
	public Event(String type, String value, Date timestamp,
			Activity activity, long atPomodoroValue) {
		this.type = type;
		this.value = value;
		this.timestamp = timestamp;
		this.activity = activity;
		this.atPomodoroValue = atPomodoroValue;
	}
	
	/**
	 * @param type event type
	 * @param value event value
	 * @param timestamp date
	 * @param activity reference to its activity
	 */
	public Event(String type, String value, Date timestamp,
			Activity activity) {
		this.type = type;
		this.value = value;
		this.timestamp = timestamp;
		this.activity = activity;
		this.atPomodoroValue = -1;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the activity
	 */
	public Activity getActivity() {
		return activity;
	}
	
	/**
	 * @param activity the activity to set
	 */
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	
	/**
	 * @return number of pomodoro
	 */
	public long getAtPomodoroValue() {
		return atPomodoroValue;
	}
	
	/**
	 * @param atPomodoroValue
	 * 
	 * The number of pomodoro to set
	 */
	public void setAtPomodoroValue(long atPomodoroValue) {
		this.atPomodoroValue = atPomodoroValue;
	}
	
}
