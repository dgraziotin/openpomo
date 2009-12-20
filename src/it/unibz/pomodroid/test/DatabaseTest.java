package it.unibz.pomodroid.test;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import it.unibz.pomodroid.persistency.*;

public class DatabaseTest extends AndroidTestCase {
	protected static DBHelper dbHelper;
	static final String LOG_TAG = "DatabaseTest";
	protected static Context context = null;
	
	public void setUp() {
		DatabaseTest.context = super.getContext();
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

	public void tearDown(){
		dbHelper.close();
	}
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
	}
}
