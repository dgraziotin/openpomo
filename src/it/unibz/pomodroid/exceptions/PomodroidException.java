package it.unibz.pomodroid.exceptions;

import android.app.AlertDialog;
import android.content.Context;

/**
 * All exception that can occur will be propagated and shown thanks to this class.
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * 
 */
public class PomodroidException extends Exception{
	
	private static final long serialVersionUID = 1L;
	private String title = null;
	

	// Default constructor 
	// initializes custom exception variable to none
	public PomodroidException(Exception e) {
		// call superclass constructor
		super();            
	}
	
	// Default constructor 
	// initializes custom exception variable to none
	public PomodroidException() {
		// call superclass constructor
		super();            
	}

	// Custom Exception Constructor
	public PomodroidException(String message) {
		// Call super class constructor
		super(message);  
	} 
	
	// Custom Exception Constructor
	public PomodroidException(String message, String title) {
		// Call super class constructor
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
	 * @param title
	 * 
	 * the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	

	/**
	 * @param context
	 * 
	 * This method shows an exeption
	 * 
	 */
	public void alertUser(Context context){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context); 
		if(this.title!=null)
			dialog.setTitle(title);
		else
			dialog.setTitle("WARNING");
		dialog.setMessage(this.getMessage());
		dialog.setNeutralButton("Ok", null);
		dialog.create().show();
	}
	
	/**
	 * @param context
	 * @param title
	 * 
	 * This method shows a simply message (information)
	 */
	public void alertUser(Context context, String title){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context); 
		dialog.setTitle(title);
		dialog.setMessage(this.getMessage());
		dialog.setNeutralButton("Ok", null);
		dialog.create().show();
	}

}
