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
 *   along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibz.pomodroid.models;
import java.net.URL;

/**
 * A class representing a Service.
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * 
 */

public class Service {
	
	private String name;
	private String url; 
	private String username;
	private String password;
	private boolean isAnonymousAccess;
	
	public Service(){
		this.name = null;
		this.url = null;
		this.username = null;
		this.password = null;
		this.isAnonymousAccess = false;
	}
	
	public Service(String name, String url, String username, String password, boolean isAnonymousAccess){
		this.name = name;
		this.url = url;
		this.username = username;
		this.password = password;
		this.isAnonymousAccess = isAnonymousAccess;
	}
	
    public Service(String name, String url, String username){
    	this.name = name;
		this.url = url;
		this.username = username;
		this.password = "";
		this.isAnonymousAccess = true;
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

}