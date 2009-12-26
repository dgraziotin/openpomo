package it.unibz.pomodroid.test;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.factories.PromFactory;
import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.PromEventDeliverer;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

public class PromTest extends AndroidTestCase {
	protected static DBHelper dbHelper;
	static final String LOG_TAG = "PromTest";
	protected static Context context = null;

	public void setUp() {
		PromTest.context = super.getContext();
		if (dbHelper == null) {
			dbHelper = new DBHelper(context);
		}
	}

	public void testGetUploadId() throws PomodroidException {
		Log.d(LOG_TAG, "testGetUploadId");
		PromEventDeliverer ped = new PromEventDeliverer();
		User user = User.retrieve(dbHelper);
		assertNotNull(user);
		int id = ped.getUploadId(user);
		assert (id == 0);
	}
	
	public void testEventCreation(){
		
	}

	public void testEntityDelivery() throws IOException, PomodroidException {
		Log.d(LOG_TAG, "testEntityDelivery");
		User user = User.retrieve(dbHelper);
		PromEventDeliverer ped = new PromEventDeliverer();
		PromFactory pf = new PromFactory();
		
		
Log.d(LOG_TAG, "testEventCreation");
		int numberPomodoro = 0;
		Date today = new Date();
		String title = "activity title";
		String summary = "activity summary";
		String origin = "trac";
		int originId = 123;
		String priority = "high";
		String reporter = "dgraziotin";
		String type = "bugfix";
		Log.d(LOG_TAG, "testEventCreation, creating an activity and an event");
		Activity activity = new Activity(numberPomodoro,today,today,title,summary,origin,originId,priority,reporter,type);
		activity.save(dbHelper);
		assertTrue(Activity.isPresent(origin, originId, dbHelper));
		
		Event event = new Event("eventType","eventValue",new Date(), activity);
		event.save(dbHelper);
		
		List<Event> eventsForActivity = Event.getAll(activity, dbHelper);
		
		assertNotNull(eventsForActivity);
		assert(eventsForActivity.size()==1);

		if (eventsForActivity.size() > 0) {
			Log.d(LOG_TAG, "testEntityDelivery, processing " + eventsForActivity.size()
					+ " events");
			byte[] zipIni = pf.createZip(eventsForActivity, user);
			int result = ped.testUploadData(zipIni, user);
			assert (eventsForActivity.size() == result);
		} else {
			assertTrue(true);
		}
		event.delete(dbHelper);
		activity.delete(dbHelper);
		
	}

	public void tearDown() {
		dbHelper.close();
	}

	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
	}
}
