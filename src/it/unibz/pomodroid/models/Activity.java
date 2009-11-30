package it.unibz.pomodroid.models;

import java.sql.Timestamp;


public class Activity {

	private int number_pomodoro;
	private Timestamp received;
	private Timestamp deadline;
	private String description;
	private String origin;
	private int origin_id;
	private String reporter;
	private String type;

	/**
	 * @param number_pomodoro
	 * @param received
	 * @param deadline
	 * @param description
	 * @param origin
	 * @param origin_id
	 * @param reporter
	 * @param type
	 */
	public Activity(int number_pomodoro, Timestamp received,
			Timestamp deadline, String description, String origin,
			int origin_id, String reporter, String type) {
		this.number_pomodoro = number_pomodoro;
		this.received = received;
		this.deadline = deadline;
		this.description = description;
		this.origin = origin;
		this.origin_id = origin_id;
		this.reporter = reporter;
		this.type = type;
	}

	/**
	 * @return the number_pomodoro
	 */
	public int getNumber_pomodoro() {
		return number_pomodoro;
	}

	/**
	 * @param numberPomodoro
	 *            the number_pomodoro to set
	 */
	public void setNumber_pomodoro(int numberPomodoro) {
		number_pomodoro = numberPomodoro;
	}

	/**
	 * @return the received
	 */
	public Timestamp getReceived() {
		return received;
	}

	/**
	 * @param received
	 *            the received to set
	 */
	public void setReceived(Timestamp received) {
		this.received = received;
	}

	/**
	 * @return the deadline
	 */
	public Timestamp getDeadline() {
		return deadline;
	}

	/**
	 * @param deadline
	 *            the deadline to set
	 */
	public void setDeadline(Timestamp deadline) {
		this.deadline = deadline;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
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
	 * @param origin
	 *            the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * @return the origin_id
	 */
	public int getOrigin_id() {
		return origin_id;
	}

	/**
	 * @param originId
	 *            the origin_id to set
	 */
	public void setOrigin_id(int originId) {
		origin_id = originId;
	}

	/**
	 * @return the reporter
	 */
	public String getReporter() {
		return reporter;
	}

	/**
	 * @param reporter
	 *            the reporter to set
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
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}