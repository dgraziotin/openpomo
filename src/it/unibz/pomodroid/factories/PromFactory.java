package it.unibz.pomodroid.factories;
import java.util.List;

import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.Activity;

public class PromFactory {
	private static final int EVENT_PLUGIN_ID = 14;
	private static final int LOCATION_PLUGIN_ID = 15;
	private static final int ANDROID_APPLICATION_ID = 1000;
	
	/**
	 * Returns the String representation of the passed timestamp in the
     * PROMDate compatible format
     * 
	 * @param milliseconds
	 * @return The String representation of the timestamp in the PROMDate format, that
     * is 'YYYYMMDDHHMMSS'
	 */
	/*

		Calendar c = Calendar.getInstance();        
	    c.setTimeInMillis(milliseconds);
	    int year = c.get(Calendar.YEAR);
	    int month = c.get(Calendar.MONTH)+1;
	    int day = c.get(Calendar.DAY_OF_MONTH);
	    int hour = c.get(Calendar.HOUR_OF_DAY);
	    int minute = c.get(Calendar.MINUTE);
	    int second = c.get(Calendar.SECOND);
	    try {
	        String y = fillLeft(String.valueOf(year), 4, '0');
	        String mo = fillLeft(String.valueOf(month), 2, '0');
	        String d = fillLeft(String.valueOf(day), 2, '0');
	        String h = fillLeft(String.valueOf(hour), 2, '0');
	        String mi = fillLeft(String.valueOf(minute), 2, '0');
	        String s = fillLeft(String.valueOf(second), 2, '0');
	                
	        String returnValue = y + mo + d + h + mi + s;        
	        return returnValue;
		}
		catch(RuntimeException re) {
			
			throw re;
		}
	}
	*/
	
	public static void promify(List<Event> events){
		
		
	}
}
