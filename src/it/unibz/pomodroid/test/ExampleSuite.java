package it.unibz.pomodroid.test;

import com.db4o.ObjectContainer;

import it.unibz.pomodroid.persistency.DBHelper;
import android.content.Context;
import junit.framework.TestSuite;

public class ExampleSuite extends TestSuite {
	private DBHelper dbHelper;
    public ExampleSuite(DBHelper dbHelper) {
    	this.addTestSuite(DatabaseTest.class);
        //addTestSuite( ContactTest.class );
    }
}
