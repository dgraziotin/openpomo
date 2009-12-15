package it.unibz.pomodroid.models;
import java.util.Date;

/**
 * @author bodom_lx
 * 
 */
public class Activity {

	private int numberPomodoro;
	private Date received;
	private Date deadline;
	private String summary;
	private String description;
	private String origin;
	private int originId;
	private String priority;
	private String reporter;
	private String type;

	/**
	 * @param number_pomodoro
	 * @param received
	 * @param deadline
	 * @param summary
	 * @param description
	 * @param origin
	 * @param origin_id
	 * @param priority
	 * @param reporter
	 * @param type
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
	}

	/**
	 * @return the number_pomodoro
	 */
	public int getNumberPomodoro() {
		return numberPomodoro;
	}

	/**
	 * @param numberPomodoro
	 *            the number_pomodoro to set
	 */
	public void setNumberPomodoro(int numberPomodoro) {
		this.numberPomodoro = numberPomodoro;
	}

	/**
	 * @return the received
	 */
	public Date getReceived() {
		return received;
	}

	/**
	 * @param received
	 *            the received to set
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
	 * @param deadline
	 *            the deadline to set
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
	public int getOriginId() {
		return originId;
	}

	/**
	 * @param originId
	 *            the origin_id to set
	 */
	public void setOriginId(int originId) {
		this.originId = originId;
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

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	

}