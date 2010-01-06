package it.unibz.pomodroid.exceptions;

import android.app.AlertDialog;
import android.content.Context;

public class PomodroidException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title = null;
	

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
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

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
	
	public void alertUser(Context context, String title){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context); 
		dialog.setTitle(title);
		dialog.setMessage(this.getMessage());
		dialog.setNeutralButton("Ok", null);
		dialog.create().show();
	}

}
