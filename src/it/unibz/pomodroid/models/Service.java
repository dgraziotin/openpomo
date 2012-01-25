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
package cc.task3.pomodroid.models;

/**
 * A class representing a Service.
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 */

public class Service {
	/**
	 * User given name for the Service
	 */
	private String name;
	/**
	 * URL for the Service
	 */
	private String url; 
	/**
	 * Service username
	 */
	private String username;
	/**
	 * Service password (if needed)
	 */
	private String password;
	/**
	 * Type of Service (Trac, BugZilla, ..)
	 */
	private String type;
	/**
	 * True if the Service does not require HTTP BASIC AUTH
	 */
	private boolean isAnonymousAccess;
	/**
	 * True if the User wants issues to be searched from it
	 */
	private boolean isActive;
	
	/**
	 * Default Constructor. Sets all fields to null
	 */
	public Service(){
		this.name = null;
		this.url = null;
		this.username = null;
		this.password = null;
		this.isAnonymousAccess = false;
		this.setType(null);
		this.isActive = true;
	}
	
	/**
	 * @param name
	 * @param url
	 * @param type
	 * @param username
	 * @param password
	 * @param isAnonymousAccess
	 */
	public Service(String name, String url, String type, String username, String password, boolean isAnonymousAccess){
		this.name = name;
		this.url = url;
		this.setType(type);
		this.username = username;
		this.password = password;
		this.isAnonymousAccess = isAnonymousAccess;
		this.isActive = true;
	}
	
	/**
	 * 
	 * @param name
	 * @param url
	 * @param type
	 * @param username
	 */
    public Service(String name, String url, String type, String username){
    	this.name = name;
		this.url = url;
		this.setType(type);
		this.username = username;
		this.password = "";
		this.isAnonymousAccess = true;
		this.isActive = true;
	}
    
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return the isAnonymousAccess
	 */
	public boolean isAnonymousAccess() {
		return isAnonymousAccess;
	}

	/**
	 * @param isAnonymousAccess the isAnonymousAccess to set
	 */
	public void setAnonymousAccess(boolean isAnonymousAccess) {
		this.isAnonymousAccess = isAnonymousAccess;
	}
	
	/**
	 * @param isActive
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	/**
	 * @return
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Helper method for updating an existing Service
	 * @param service
	 */
	public void update(Service service){
		this.name = service.getName();
		this.url = service.getUrl();
		this.type = service.getType();
		this.username = service.getUsername();
		this.password = service.getPassword();
		this.isAnonymousAccess = service.isAnonymousAccess();
		this.isActive = service.isActive();
	}

}