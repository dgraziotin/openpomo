package it.unibz.pomodroid.test;

import android.content.Context;

public class TestSuite extends junit.framework.TestSuite {
	public Context context = null;
    public TestSuite() {
    	PersistencyTest db = new PersistencyTest();
    	db.setContext(context);
        addTestSuite( PersistencyTest.class );
        addTestSuite( XmlRpcTest.class );
        addTestSuite( TracTest.class );
        addTestSuite( PromTest.class );
    }
}
