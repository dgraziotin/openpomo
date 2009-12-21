package it.unibz.pomodroid.models;

/**
 * 
 * @author Thomas Schievenin
 *
 * A class representing a tipical pomodroid user. Each user has its username (string), password (tring), 
 * trac url (absolute url), prom url (absolute url).
 * 
 */

public class User {
	
	private String tracUsername;
	private String tracPassword;
	private String tracUrl;
	private String promUrl;
	
	/**
	 * @param tracUsername pomodroid username
	 * @param tracPassword pomodroid password
	 * @param tracUrl absolute trac url
	 * @param promUrl absolute prom url
	 */
	public User(String tracUsername, String tracPassword, String tracUrl, String promUrl) {
		this.tracUsername = tracUsername;
		this.tracPassword = tracPassword;
		this.tracUrl = tracUrl;
		this.promUrl = promUrl;
	}

	/**
	 * @return the absolute prom Url
	 */
	public String getPromUrl() {
		return promUrl;
	}

	/**
	 * @param promUrl the promUrl to set
	 */
	public void setPromUrl(String promUrl) {
		this.promUrl = promUrl;
	}

	/**
	 * @return the trac username
	 */
	public String getTracUsername() {
		return tracUsername;
	}

	/**
	 * @param tracUsername the tracUsername to set
	 */
	public void setTracUsername(String tracUsername) {
		this.tracUsername = tracUsername;
	}

	/**
	 * @return the trac password
	 */
	public String getTracPassword() {
		return tracPassword;
	}

	/**
	 * @param tracPassword the trac password to set
	 */
	public void setTracPassword(String tracPassword) {
		this.tracPassword = tracPassword;
	}

	/**
	 * @return the tracUrl
	 */
	public String getTracUrl() {
		return tracUrl;
	}

	/**
	 * @param tracUrl the trac Url to set
	 */
	public void setTracUrl(String tracUrl) {
		this.tracUrl = tracUrl;
	}
	
	
}
