package it.unibz.pomodroid.test;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.PromEventDeliverer;
import it.unibz.pomodroid.services.XmlRpcClient;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

public class XmlRpcTest extends AndroidTestCase{
		static final String LOG_TAG = "XmlRpcTest";
		private static User user = null;
		protected static DBHelper dbHelper;
		protected static Context context = null;
		public void setUp() throws PomodroidException{
			XmlRpcTest.context = super.getContext();
			if(dbHelper == null){
				dbHelper = new DBHelper(context);
			}
			if(user==null){
				user = User.retrieve(dbHelper);
			}
		}

		public void testTracConnection() throws PomodroidException{
			Log.d(LOG_TAG, "testTracConnection");
			Object[] params = {};
			// it should return an integer > 0
			Object[] result = XmlRpcClient.fetchMultiResults(user.getTracUrl(), user.getTracUsername(), user.getTracPassword(), "system.listMethods", params);
			assertTrue(result.length > 0);
		}
		
		public void testPromConnection() throws Exception{
			Log.d(LOG_TAG, "testPromConnection");
			PromEventDeliverer ped = new PromEventDeliverer();
			int id = ped.getUploadId(user);
			assert(id > 0);
		}
		
		public void tearDown(){
			dbHelper.commit();
		}

		public void testAndroidTestCaseSetupProperly() {
			super.testAndroidTestCaseSetupProperly();
			Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
		}
	
}
