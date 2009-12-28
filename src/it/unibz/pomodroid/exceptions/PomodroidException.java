package it.unibz.pomodroid.exceptions;
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
	
	// Custom Exception Constructor
	public PomodroidException(String message, String type) {
		super(type+":"+message);
		
	} 



}
