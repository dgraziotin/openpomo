package it.unibz.pomodroid.models;
import java.util.Date;

/**
 * @author Thomas Schievenin
 * 
 * A class representing an activity. Each activity is described by its number of pomodoro (integer number),
 * received date (date), deadline (date), summary (string), description (string), origin (string i.e. trac), 
 * origin id (integer that refers to its origin), priority (string), reporter (string), type (string), 
 * todoToday (boolean), done (boolean) and endDate (date).
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
	private boolean todoToday;
	private boolean done;
	private Date endDate;

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
	
	public Activity (String origin, int originId) {
		this.origin = origin;
		this.originId = originId;
	}
	
	public void update (Activity ac) {
		this.numberPomodoro = ac.getNumberPomodoro();
		// this.deadline = ac.getDeadline();
		this.summary = ac.getSummary();
		// this.description = ac.getDescription();
		// this.origin = ac.getOrigin();
		// this.originId = ac.getOriginId();
		// this.priority = ac.getPriority();
		// this.reporter = ac.getReporter();
		// this.type = ac.getType();
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
	 * @param numberPomodoro
	 *            sets the number of pomodoro
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
	 * @param received
	 *            received date to set
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
	 *            deadline to set
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
	 *            description to set
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
	 *            origin to set
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
	 * @param originId
	 *            origin_id to set
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
	 * @param reporter
	 *            reporter name to set
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

	
	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	
	/**
	 * @param summary
	 * 			summary to set
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
	 * @param priority
	 * 			the priority to set
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
	 * @param todoToday
	 * 			the todoToday to set
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
		return endDate;
	}

	/**
	 * @param date
	 * 			the date to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void setUndone() {
		this.done = false;
	}

	public String getShortDescription(){
	   final int maxLength = 25;
	   if (this.description.length()>maxLength)
		 return description.substring(0, maxLength).replaceAll("\n", " ")+"...";
	   return this.description.replaceAll("\n", " ");
	}
	
	public void addOnePomodoro (){
		this.numberPomodoro++;
	}
}