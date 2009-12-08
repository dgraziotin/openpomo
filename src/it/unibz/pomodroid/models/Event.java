package it.unibz.pomodroid.models;

import java.security.Timestamp;

public class Event {

	private String type;
	private String value;
	private Timestamp timestamp;
	private Activity activity;
	
	
	public Event(String type, String value, Timestamp timestamp,
			Activity activity) {
		this.type = type;
		this.value = value;
		this.timestamp = timestamp;
		this.activity = activity;
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
	public Timestamp getTimestamp() {
		return timestamp;
	}
	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Timestamp timestamp) {
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
	
	
	
	
	
}
