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

import java.util.List;

import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.Service;
import it.unibz.pomodroid.persistency.User;
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
			List<Service> services = Service.getAll(dbHelper);
			if(services==null || services.isEmpty()){
				assertTrue(true);
				return;
			}
			Service service = services.get(0);
			Log.d(LOG_TAG, "testTracConnection");
			Object[] params = {};
			// it should return an integer > 0
			Object[] result = XmlRpcClient.fetchMultiResults(service.getUrl(), service.getUsername(), service.getPassword(), "system.listMethods", params);
			assertTrue(result.length > 0);
		}
		
		
		public void tearDown(){
			dbHelper.commit();
		}

		public void testAndroidTestCaseSetupProperly() {
			super.testAndroidTestCaseSetupProperly();
			Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
		}
	
}
