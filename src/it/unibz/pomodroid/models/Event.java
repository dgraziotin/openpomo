package it.unibz.pomodroid.models;

import java.security.Timestamp;

public class Event {

	private String type;
	private Timestamp datetime;
	
	public Event(String type, Timestamp datetime) {
		this.type = type;
		this.datetime = datetime;
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
	
}
