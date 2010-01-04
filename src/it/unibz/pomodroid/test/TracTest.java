package it.unibz.pomodroid.test;

import java.util.HashMap;
import java.util.Vector;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.factories.ActivityFactory;
import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.TrackTicketFetcher;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

public class TracTest extends AndroidTestCase{
	protected static DBHelper dbHelper;
	static final String LOG_TAG = "TrackTicketFetcherTest";
	protected static Context context = null;
	private TrackTicketFetcher ttf = new TrackTicketFetcher();
	private ActivityFactory af = new ActivityFactory();
	public void setUp() {
		TracTest.context = super.getContext();
		if(dbHelper == null){
			dbHelper = new DBHelper(context);
		}
	}
	
	public void testNumberTickets() throws PomodroidException{
		Log.d(LOG_TAG, "testNumberTickets");
		User user = User.retrieve(dbHelper);
		int numberTickets = ttf.getNumberTickets(user);
		assertTrue(numberTickets >= 0);
	}
	
	public void testTicketRetrieval() throws PomodroidException{
		Log.d(LOG_TAG, "testTicketRetrieval");
		User user = User.retrieve(dbHelper);
		int numberTickets = ttf.getNumberTickets(user);
		int numberActivities = Activity.getNumberActivities(dbHelper);
		if(numberTickets > numberActivities){
			Vector<HashMap<String, Object>>  tickets = ttf.fetch(user, dbHelper);
			assertNotNull(tickets);
			assert(tickets.size() > 0);
		}else{
			assertTrue(true);
		}
	}
	
	public void testActivityFactory() throws PomodroidException{
		Log.d(LOG_TAG, "testActivityFactory");
		User user = User.retrieve(dbHelper);
		int numberTickets = ttf.getNumberTickets(user);
		int numberActivities = Activity.getNumberActivities(dbHelper);
		Vector<HashMap<String, Object>> tickets = ttf.fetch(user, dbHelper);
		if(numberTickets > numberActivities){
			int activitiesStored = af.produce(tickets, dbHelper);
			assert(activitiesStored > 0);
			while (tickets.size() > 0){
				String origin = user.getTracUrl();
				int originId = (Integer) tickets.get(0).get("originId");
				Activity ac = Activity.getActivity(origin, originId, dbHelper);
				ac.delete(dbHelper);
			}
		}else{
			assertTrue(true);
		}
	}
	
	public void tearDown(){
		dbHelper.close();
	}
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
	}
}
