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
package it.unibz.pomodroid.test;

import java.util.HashMap;
import java.util.Vector;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.factories.ActivityFactory;
import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.Service;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.TracTicketFetcher;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

public class TracTest extends AndroidTestCase{
	protected static DBHelper dbHelper;
	static final String LOG_TAG = "TrackTicketFetcherTest";
	protected static Context context = null;
	private TracTicketFetcher ttf = new TracTicketFetcher();
	private ActivityFactory af = new ActivityFactory();
	public void setUp() {
		TracTest.context = super.getContext();
		if(dbHelper == null){
			dbHelper = new DBHelper(context);
		}
	}
	
	public void testNumberTickets() throws PomodroidException{
		Log.d(LOG_TAG, "testNumberTickets");
		Service service = Service.getAll(dbHelper).get(0);
		int numberTickets = ttf.getNumberTickets(service);
		assertTrue(numberTickets >= 0);
	}
	
	public void testTicketRetrieval() throws PomodroidException{
		Log.d(LOG_TAG, "testTicketRetrieval");
		User user = User.retrieve(dbHelper);
		Service service = Service.getAll(dbHelper).get(0);
		int numberTickets = ttf.getNumberTickets(service);
		int numberActivities = Activity.getNumberActivities(dbHelper);
		if(numberTickets > numberActivities){
			Vector<HashMap<String, Object>>  tickets = ttf.fetch(service, dbHelper);
			assertNotNull(tickets);
			assert(tickets.size() > 0);
		}else{
			assertTrue(true);
		}
	}
	
	public void testActivityFactory() throws PomodroidException{
		Log.d(LOG_TAG, "testActivityFactory");
		User user = User.retrieve(dbHelper);
		Service service = Service.getAll(dbHelper).get(0);
		int numberTickets = ttf.getNumberTickets(service);
		int numberActivities = Activity.getNumberActivities(dbHelper);
		Vector<HashMap<String, Object>> tickets = ttf.fetch(service, dbHelper);
		if(numberTickets > numberActivities){
			int activitiesStored = af.produceTest(tickets, dbHelper);
			assert(activitiesStored > 0);
		}else{
			assertTrue(true);
		}
	}
	
	public void tearDown(){
		dbHelper.commit();
	}
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
	}
}
