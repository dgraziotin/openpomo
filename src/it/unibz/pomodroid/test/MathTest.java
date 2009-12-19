package it.unibz.pomodroid.test;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import it.unibz.pomodroid.persistency.*;

public class MathTest extends AndroidTestCase {
	protected int i1;
	protected int i2;
	static final String LOG_TAG = "MathTest";
	static Context context;

	public void setUp() {
		i1 = 2;
		i2 = 3;
		
	}
	
	public void testDatabase(){
		Log.d(LOG_TAG, "testDatabase");
		User user = User.retrieve();
		assertTrue(user.getTracUsername().equals("dgraziotin"));
	}

	public void testGnek() {
		Log.d(LOG_TAG, "testGnek");
		assertTrue(LOG_TAG + "3", true);
	}

	public void testBlah() {
		Log.d(LOG_TAG, "testBlah");
		assertTrue(LOG_TAG + "2", true);
	}

	public void testAdd() {
		Log.d(LOG_TAG, "testAdd");
		assertTrue(LOG_TAG + "1", ((i1 + i2) == 5));
	}

	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
	}
}
