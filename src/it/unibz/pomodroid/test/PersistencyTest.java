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

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.*;

public class PersistencyTest extends AndroidTestCase {
	protected static DBHelper dbHelper;
	static final String LOG_TAG = "DatabaseTest";
	protected static Context context = null;
	private int numberOriginalActivities = 0;
	
	public void setUp() throws PomodroidException {
		PersistencyTest.context = super.getContext();
		if(dbHelper == null){
			dbHelper = new DBHelper(context);
		}
		try {
			numberOriginalActivities = Activity.getNumberActivities(dbHelper);
		} catch (PomodroidException e) {
			Log.e("PersistencyTest.setUp()", e.toString());
			throw new PomodroidException("ERROR in PersistencyTest.setUp(): "+e.getMessage());
		}
	}
	
	public void testContext(){
		Log.d(LOG_TAG, "testContext");
		assertNotNull(context);
	}
	
	public void testDatabaseConnection() throws PomodroidException{
		Log.d(LOG_TAG, "testDatabaseConnection");
		assertNotNull(dbHelper.getDatabase());
	}
	
	public void testUserPresence() throws PomodroidException{
		Log.d(LOG_TAG, "testUserPresence");
		assertNotNull(User.retrieve(dbHelper));
	}
	
	public void testUserEdit() throws PomodroidException{
		Log.d(LOG_TAG, "testUserEdit");
		
		User user = User.retrieve(dbHelper);
		int oldPomodoroDuration = user.getPomodoroMinutesDuration();
		user.setPomodoroMinutesDuration(99);
		user.save(dbHelper);
		
		user = User.retrieve(dbHelper);
		assertTrue(user.getPomodoroMinutesDuration() == 99);
		
		user.setPomodoroMinutesDuration(oldPomodoroDuration);
		user.save(dbHelper);
		
		user = User.retrieve(dbHelper);
		assertTrue(user.getPomodoroMinutesDuration() == oldPomodoroDuration);
	}
	
	public void testActivityCreation() throws PomodroidException{
		Log.d(LOG_TAG, "testActivityCreation");
		int numberPomodoro = 0;
		Date today = new Date();
		String title = "TEST";
		String summary = "TEST";
		String origin = "TEST";
		int originId = 123;
		String priority = "TEST";
		String reporter = "TEST";
		String type = "TEST";
		Activity ac = new Activity(numberPomodoro,today,today,title,summary,origin,originId,priority,reporter,type);
		ac.save(dbHelper);
		assertTrue(Activity.isPresent(origin, originId, dbHelper));
		ac.delete(dbHelper);
		int numberActivities = Activity.getNumberActivities(dbHelper);
		assertTrue(numberActivities==this.numberOriginalActivities);
	}
	
	public void testEventCreation() throws PomodroidException{
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
		
		Activity activity = new Activity(numberPomodoro,today,today,title,summary,origin,originId,priority,reporter,type);
		activity.save(dbHelper);
		assertTrue(Activity.isPresent(origin, originId, dbHelper));
		
		Event event = new Event("eventType","eventValue",new Date(), activity);
		event.save(dbHelper);
		
		List<Event> eventsForActivity = Event.getAll(activity, dbHelper);
		
		assertNotNull(eventsForActivity);
		assert(eventsForActivity.size()==1);
		
		Event.delete(activity, dbHelper);
		
		activity.delete(dbHelper);
		int numberActivities = Activity.getNumberActivities(dbHelper);
		assertTrue(numberActivities==this.numberOriginalActivities);
	}


	public void tearDown(){
		dbHelper.commit();
	}
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
	}
}
