package it.unibz.pomodroid.test;

import java.util.Date;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import it.unibz.pomodroid.persistency.*;

public class PersistencyTest extends AndroidTestCase {
	protected static DBHelper dbHelper;
	static final String LOG_TAG = "DatabaseTest";
	protected static Context context = null;
	
	public void setUp() {
		PersistencyTest.context = super.getContext();
		if(dbHelper == null){
			dbHelper = new DBHelper(context);
		}
	}
	
	public void testContext(){
		Log.d(LOG_TAG, "testContext");
		assertNotNull(context);
	}
	
	public void testDatabaseConnection(){
		Log.d(LOG_TAG, "testDatabaseConnection");
		assertNotNull(dbHelper.getDatabase());
	}
	
	public void testUserPresence(){
		Log.d(LOG_TAG, "testUserPresence");
		assertNotNull(User.retrieve(dbHelper));
	}
	
	public void testUserEdit(){
		Log.d(LOG_TAG, "testUserEdit");
		
		User user = User.retrieve(dbHelper);
		String oldUsername = user.getTracUsername();
		user.setTracUsername("testtest");
		user.save(dbHelper);
		
		user = User.retrieve(dbHelper);
		assertTrue(user.getTracUsername().equals("testtest"));
		
		user.setTracUsername(oldUsername);
		user.save(dbHelper);
		
		user = User.retrieve(dbHelper);
		assertTrue(user.getTracUsername().equals(oldUsername));
	}
	
	public void testActivityCreation(){
		Log.d(LOG_TAG, "testActivityCreation");
		int numberPomodoro = 0;
		Date today = new Date();
		String title = "activity title";
		String summary = "activity summary";
		String origin = "trac";
		int originId = 123;
		String priority = "high";
		String reporter = "dgraziotin";
		String type = "bugfix";
		Activity ac = new Activity(numberPomodoro,today,today,title,summary,origin,originId,priority,reporter,type);
		ac.save(dbHelper);
		assertTrue(Activity.isPresent(origin, originId, dbHelper));
	}
	
	// TODO: test Event creation
	// TODO: test Event related to an activity

	public void tearDown(){
		dbHelper.close();
	}
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
	}
}
