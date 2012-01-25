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
package cc.task3.pomodroid.exceptions;

import android.content.Context;
import android.widget.Toast;

/**
 * All exception that can occur will be propagated and shown thanks to this class.
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 * 
 */
public class PomodroidException extends Exception{
	
	/**
	 * Required by the compiler
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * A Custom title for an Exception when displayed to User
	 */
	private String title = null;
	

	/**
	 * Default constructor 
	 */
	public PomodroidException(Exception e) {
		// call superclass constructor
		super();            
	}
	
	/**
	 * Default constructor 
	 */
	public PomodroidException() {
		// call superclass constructor
		super();            
	}

	/**
	 * Constructor for displaying a custom message
	 */
	public PomodroidException(String message) {
		// Call super class constructor
		super(message);  
	} 
	
	/**
	 * Constructor for displaying a custom message and title
	 */
	public PomodroidException(String message, String title) {
		super(message);  
		this.title = title;
	} 
	
	/**
	 * @return title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * the title to set
	 * @param title
	 * 
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	

	/**
	 * This method shows an exception to user, with a Toast
	 * @param context
	 * 
	 */
	public void alertUser(Context context){
		Toast.makeText(context, this.getMessage(), Toast.LENGTH_LONG).show();
	}
	
	/**
	 * This method shows a simply message (information)
	 * @param context
	 * @param title
	 *
	 */
	public void alertUser(Context context, String title){
		Toast.makeText(context, title+": "+this.getMessage(), Toast.LENGTH_LONG).show();
	}
	
	/**
	 * This method shows a simply message (information)
	 * @param context
	 * @param title
	 *
	 */
	public static void createAlert(Context context, String title, String message){
		Toast.makeText(context, title+": "+ message, Toast.LENGTH_LONG).show();
	}

}
