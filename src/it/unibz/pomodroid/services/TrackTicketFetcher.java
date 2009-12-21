package it.unibz.pomodroid.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import android.util.Log;
import it.unibz.pomodroid.factories.ActivityFactory;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;


/**
 * @author Thomas Schievenin
 * 
 * A class that retrieves tickets from TRAC. Initially, this class takes all opened tickets, than theirs information.
 * Finally it calls the class activity and store them into the db. 
 *
 */

public class TrackTicketFetcher {

	
	/**
	 * @param user user object
	 * @param dbHelper connection to the db
	 * 
	 * It retrieves all opened tickets from TRAC
	 */
	public static void fetch (User user, DBHelper dbHelper){
		Vector<Integer> ticketIds;
		ticketIds = getTicketIds(user);
		
		HashMap<String,String> attributes;
		Date deadLine = new Date();
	
		for (Integer ticketId : ticketIds) {
		   Integer[] id = {ticketId};
		   attributes = getTickets(user, id);
		   if (!(attributes.get("milestone").toString().equals(""))){
		     deadLine = getDeadLine(user, attributes.get("milestone").toString());
		   }
		   // FIXME: we should not call ActivityFactory from here, TrackTicketFetcher must be independent
		   ActivityFactory.produce((int)ticketId, deadLine, attributes, dbHelper);
		}
		Log.i("TrackTicketFetcher", "Tickets are now in the DB");
	}
	
	
	/**
	 * @param user user object
	 * @return vector 
	 */
	public static Vector<Integer> getTicketIds (User user){
		String[] params = {"status!=closed"};
		Object[] result = XmlRpcClient.fetchMultiResults(user.getTracUrl(),user.getTracUsername(),user.getTracPassword(), "ticket.query",params);
		if (result != null){
			Vector<Integer> ticketsIds = new Vector<Integer>();
			for (Object i : result)
				ticketsIds.addElement(new Integer(i.toString()));
			return ticketsIds;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	// FIXME: the method should not be static
	public static HashMap<String,String> getTickets(User user, Integer[] id){
		Object[] ticket;
		HashMap<String,String> attributes;
		ticket = XmlRpcClient.fetchMultiResults(user.getTracUrl(),user.getTracUsername(),user.getTracPassword(), "ticket.get",id);
		attributes = (HashMap<String,String>) ticket[3];
		return attributes;
	}
	
	/**
	 * @param user
	 * @param milestoneId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	// FIXME: the method should not be static and should be private
	public static Date getDeadLine (User user, String milestoneId){
		Date result;
		Object dataObject;
		String params[] = {milestoneId};
		Object milestone = XmlRpcClient.fetchSingleResult(user.getTracUrl(),user.getTracUsername(),user.getTracPassword(), "ticket.milestone.get",params);
		dataObject = ((HashMap<String, Object>) milestone).get("due");
		 if (dataObject.equals(0))
		   result = new Date();
		 else
		   result = (Date) dataObject;
		return result;
	}
}
