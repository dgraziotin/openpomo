package it.unibz.pomodroid.exceptions;

import android.app.AlertDialog;
import android.content.Context;

public class PomodroidException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
	

	public void alertUser(Context context){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context); 
		dialog.setTitle("WARNING");
		dialog.setMessage(this.toString());
		dialog.setNeutralButton("Ok", null);
		dialog.create().show();
	}
	
	public void alertUser(Context context, String type){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context); 
		dialog.setTitle(type);
		dialog.setMessage(this.getMessage());
		dialog.setNeutralButton("Ok", null);
		dialog.create().show();
	}

}
