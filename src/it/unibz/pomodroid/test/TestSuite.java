package it.unibz.pomodroid.test;

import android.content.Context;

public class TestSuite extends junit.framework.TestSuite {
	public Context context = null;
    public TestSuite() {
    	DatabaseTest db = new DatabaseTest();
    	db.setContext(context);
        addTestSuite( DatabaseTest.class );
        addTestSuite( XmlRpcTest.class );
        addTestSuite( TrackTicketFetcherTest.class );
    }
}
