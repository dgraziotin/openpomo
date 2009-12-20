package it.unibz.pomodroid.test;

import it.unibz.pomodroid.persistency.DBHelper;
import it.unibz.pomodroid.persistency.User;
import it.unibz.pomodroid.services.TrackTicketFetcher;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

public class TrackTicketFetcherTest extends AndroidTestCase{
	protected static DBHelper dbHelper;
	static final String LOG_TAG = "TrackTicketFetcherTest";
	protected static Context context = null;
	
	public void setUp() {
		DatabaseTest.context = super.getContext();
		if(dbHelper == null){
			dbHelper = new DBHelper(context);
		}
	}
	
	// TODO: add methods to test ticket fetch, must wait for changes to code
	/*
	public void testFetch(){
		User real = User.retrieve(dbHelper);
		User fake = new User("fakeUser","fakePassword",real.getTracUrl(),real.getPromUrl());
		TrackTicketFetcher.fetch(fake, dbHelper);
	}
	*/
	
	public void tearDown(){
		dbHelper.close();
	}
	public void testAndroidTestCaseSetupProperly() {
		super.testAndroidTestCaseSetupProperly();
		Log.d(LOG_TAG, "testAndroidTestCaseSetupProperly");
	}
}
