package it.unibz.pomodroid.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import android.util.Log;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;

/**
 * 
 * A class that retrieves tickets from TRAC. Initially, this class takes all opened tickets, than theirs information.
 * Finally it calls the class activity and store them into the db. 
 *
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 */

public class TracTicketFetcher {

	
	/**
	 * It retrieves all opened tickets from TRAC and returns them
	 * 
	 * @param user user object
	 * @param dbHelper connection to the db
	 * @return tickets from TRAC
	 *
	 * @throws PomodroidException 
	 */
	public Vector<HashMap<String, Object>> fetch (User user, DBHelper dbHelper) throws PomodroidException{
		Vector<HashMap<String, Object>> tickets = new Vector<HashMap<String, Object>>(); 
		
		Vector<Integer> ticketIds;
		ticketIds = getTicketIds(user);
		
		HashMap<String,String> attributes;
		Date deadLine = new Date();
	
		for (Integer ticketId : ticketIds) {
		   HashMap<String, Object> ticket = new HashMap<String, Object>();
		   Integer[] id = {ticketId};
		   attributes = getTickets(user, id);
		   
		   if (!(attributes.get("milestone").toString().equals(""))){
		     deadLine = getDeadLine(user, attributes.get("milestone").toString());
		   }
		   
		   ticket.put("originId", (int)ticketId);
		   ticket.put("origin", user.getTracUrl());
		   ticket.put("deadLine", deadLine);
		   ticket.put("summary",attributes.get("summary"));
		   ticket.put("description",attributes.get("description"));
		   ticket.put("priority",attributes.get("priority"));
		   ticket.put("reporter",attributes.get("reporter"));
		   ticket.put("type",attributes.get("type"));

		   tickets.add(ticket);
		   
		}
		Log.i("TrackTicketFetcher", "Returning Tickets");
		return tickets;
	}
	
	
	/**
	 * @param user user object
	 * @return vector 
	 * @throws PomodroidException 
	 */
	private Vector<Integer> getTicketIds (User user) throws PomodroidException{
		String[] params = {"status!=closed&owner="+user.getTracUsername()};
		Object[] result = XmlRpcClient.fetchMultiResults(user.getTracUrl(),user.getTracUsername(),user.getTracPassword(), "ticket.query",params);
		if (result != null){
			Vector<Integer> ticketsIds = new Vector<Integer>();
			for (Object i : result)
				ticketsIds.addElement(new Integer(i.toString()));
			return ticketsIds;
		}
		return null;
	}
	
	/**
	 * @param user
	 * @return
	 * @throws PomodroidException
	 */
	public int getNumberTickets(User user) throws PomodroidException{
		Vector<Integer> ticketIds = this.getTicketIds(user);
		return ((ticketIds == null) ?  0 :  ticketIds.size());
	}
	
	@SuppressWarnings("unchecked")
	// FIXME: the method should not be static
	private static HashMap<String,String> getTickets(User user, Integer[] id) throws PomodroidException{
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
	 * @throws PomodroidException 
	 */
	@SuppressWarnings("unchecked")
	private  Date getDeadLine (User user, String milestoneId) throws PomodroidException{
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
