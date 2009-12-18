package it.unibz.pomodroid.test;

import android.content.Context;
import junit.framework.TestSuite;

public class ExampleSuite extends TestSuite {
	public Context context = null;
    public ExampleSuite() {
    	MathTest.context = context;
        addTestSuite( MathTest.class );
        //addTestSuite( ContactTest.class );
    }
}
