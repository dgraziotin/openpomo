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

import android.content.Context;

public class TestSuite extends junit.framework.TestSuite {
	public Context context = null;
    public TestSuite() {
    	PersistencyTest db = new PersistencyTest();
    	db.setContext(context);
        addTestSuite( PersistencyTest.class );
        addTestSuite( XmlRpcTest.class );
        addTestSuite( TracTest.class );
    }
}
