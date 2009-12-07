package it.unibz.pomodroid.models;

import java.security.Timestamp;

public class Event {

	private String type;
	private Timestamp datetime;
	private Activity activity;
	
	public Event(String type, Timestamp datetime, Activity activity) {
		this.type = type;
		this.datetime = datetime;
		this.setActivity(activity);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Timestamp getDatetime() {
		return datetime;
	}
	
	public void setDatetime(Timestamp datetime) {
		this.datetime = datetime;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}
	
}
