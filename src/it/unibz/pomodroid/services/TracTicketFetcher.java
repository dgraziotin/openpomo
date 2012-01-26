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
package it.unibz.pomodroid.services;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.models.*;

import android.util.Log;

/**
 * A class that retrieves tickets from TRAC. Initially, this class takes all opened tickets, than theirs information.
 * Finally it calls the class activity and store them into the db.
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 */

public class TracTicketFetcher {


    /**
     * It retrieves all opened tickets from TRAC and returns them
     *
     * @param service  user object
     * @param dbHelper connection to the db
     * @return tickets from TRAC
     * @throws PomodroidException
     */
    public Vector<HashMap<String, Object>> fetch(Service service, DBHelper dbHelper) throws PomodroidException {
        Vector<HashMap<String, Object>> tickets = new Vector<HashMap<String, Object>>();

        Vector<Integer> ticketIds;
        ticketIds = getTicketIds(service);

        Vector<Integer> notFetchedTicketIds = this.getNotAlreadyFetchedTicketIds(service, ticketIds, dbHelper);

        HashMap<String, String> attributes;
        Date deadLine = new Date();

        for (Integer ticketId : notFetchedTicketIds) {
            HashMap<String, Object> ticket = new HashMap<String, Object>();
            Integer[] id = {ticketId};
            attributes = getTickets(service, id);

            if (!(attributes.get("milestone").toString().equals(""))) {
                deadLine = getDeadLine(service, attributes.get("milestone").toString());
            }

            ticket.put("originId", (int) ticketId);
            ticket.put("origin", service.getUrl());
            ticket.put("deadLine", deadLine);
            ticket.put("summary", attributes.get("summary"));
            ticket.put("description", attributes.get("description"));
            ticket.put("priority", attributes.get("priority"));
            ticket.put("reporter", attributes.get("reporter"));
            ticket.put("type", attributes.get("type"));

            tickets.add(ticket);

        }
        Log.i("TrackTicketFetcher", "Returning Tickets");
        return tickets;
    }


    /**
     * @param service user object
     * @return vector
     * @throws PomodroidException
     */
    private Vector<Integer> getTicketIds(Service service) throws PomodroidException {
        String[] params = {"status!=closed&owner=" + service.getUsername()};
        Object[] result = null;

        if (service.isAnonymousAccess())
            result = XmlRpcClient.fetchMultiResults(service.getUrl(), "ticket.query", params);
        else
            result = XmlRpcClient.fetchMultiResults(service.getUrl(), service.getUsername(), service.getPassword(), "ticket.query", params);

        if (result != null) {
            Vector<Integer> ticketsIds = new Vector<Integer>();
            for (Object i : result)
                ticketsIds.addElement(new Integer(i.toString()));
            return ticketsIds;
        }
        return null;
    }

    /**
     * @param service user object
     * @return vector
     * @throws PomodroidException
     */
    private Vector<Integer> getNotAlreadyFetchedTicketIds(Service service, Vector<Integer> retrievedTicketIds, DBHelper dbHelper) throws PomodroidException {
        List<Activity> storedActivitiesForService = Activity.getForService(service, dbHelper);
        HashSet<Integer> storedActivitiesIdsForService = new HashSet<Integer>(Activity.getOriginIDs(storedActivitiesForService));
        HashSet<Integer> retrievedActivitiesIdsForService = new HashSet<Integer>(retrievedTicketIds);
        retrievedActivitiesIdsForService.removeAll(storedActivitiesIdsForService);
        return new Vector<Integer>(retrievedActivitiesIdsForService);
    }

    /**
     * @param user
     * @return
     * @throws PomodroidException
     */
    public int getNumberTickets(Service service) throws PomodroidException {
        Vector<Integer> ticketIds = this.getTicketIds(service);
        return ((ticketIds == null) ? 0 : ticketIds.size());
    }

    @SuppressWarnings("unchecked")
    // FIXME: the method should not be static
    private static HashMap<String, String> getTickets(Service service, Integer[] id) throws PomodroidException {
        Object[] ticket;
        HashMap<String, String> attributes;
        if (service.isAnonymousAccess())
            ticket = XmlRpcClient.fetchMultiResults(service.getUrl(), "ticket.get", id);
        else
            ticket = XmlRpcClient.fetchMultiResults(service.getUrl(), service.getUsername(), service.getPassword(), "ticket.get", id);


        attributes = (HashMap<String, String>) ticket[3];
        return attributes;
    }

    /**
     * @param service
     * @param milestoneId
     * @return
     * @throws PomodroidException
     */
    @SuppressWarnings("unchecked")
    private Date getDeadLine(Service service, String milestoneId) throws PomodroidException {
        Date result;
        Object dataObject;
        String params[] = {milestoneId};
        Object milestone = null;
        if (service.isAnonymousAccess())
            milestone = XmlRpcClient.fetchSingleResult(service.getUrl(), "ticket.milestone.get", params);
        else
            milestone = XmlRpcClient.fetchSingleResult(service.getUrl(), service.getUsername(), service.getPassword(), "ticket.milestone.get", params);

        dataObject = ((HashMap<String, Object>) milestone).get("due");
        if (dataObject.equals(0))
            result = new Date();
        else
            result = (Date) dataObject;
        return result;
    }
}
