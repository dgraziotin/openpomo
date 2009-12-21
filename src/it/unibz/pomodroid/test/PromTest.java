package it.unibz.pomodroid.test;

import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.PromEventDeliverer;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

public class PromTest extends AndroidTestCase{
	protected static DBHelper dbHelper;
	static final String LOG_TAG = "PromTest";
	protected static Context context = null;
	
	public void setUp() {
		PromTest.context = super.getContext();
		if(dbHelper == null){
			dbHelper = new DBHelper(context);
		}
	}
	
	public void testGetUploadId(){
		Log.d(LOG_TAG, "testGetUploadId");
		PromEventDeliverer ped = new PromEventDeliverer();
		User user = User.retrieve(dbHelper);
		assertNotNull(user);
		int id = ped.getUploadId(user);
		assert(id > 0);
	}
	
	public void tearDown(){
		dbHelper.close();
	}
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
	}
}
