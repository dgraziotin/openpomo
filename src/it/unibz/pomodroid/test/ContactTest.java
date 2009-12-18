package it.unibz.pomodroid.test;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts;
import android.util.Log;
import android.test.AndroidTestCase;

public class ContactTest extends AndroidTestCase {
    static final String LOG_TAG = "ContactTest";
    static final String TESTUSER_NAME = "Test User";
    static final String TESTUSER_NOTES = "Test note";
    ContentResolver contentResolver;
    Uri newPerson;

    public void setUp() {
        contentResolver = getContext().getContentResolver();
        ContentValues person = new ContentValues();
        person.put(Contacts.People.NAME, TESTUSER_NAME );
        person.put(Contacts.People.NOTES, TESTUSER_NOTES );

        newPerson = contentResolver.insert(
                    Contacts.People.CONTENT_URI,person);
    }

    public void testInsertContact() {
        Log.d( LOG_TAG, "testInsertContact" );
        assertNotNull( newPerson );
    }

    public void testQueryContact() {
        Log.d( LOG_TAG, "testQueryContact" );
    	String columns[] = { Contacts.People.NAME,
                            Contacts.People.NOTES };
        Cursor c = contentResolver.query( Contacts.People.CONTENT_URI,
                    columns, 
                    Contacts.People.NAME+"=?",
                    new String[] { TESTUSER_NAME },
                    null );
        assertNotNull( c );
        int hits = 0;
        while( c.moveToNext() ) {
            int nameColumnIndex = c.getColumnIndex( Contacts.People.NAME );
            int notesColumnIndex = c.getColumnIndex( Contacts.People.NOTES );
	        String name = c.getString( nameColumnIndex );
            String notes = c.getString( notesColumnIndex );
            Log.d( LOG_TAG,"retrieved name: "+name );
            Log.d( LOG_TAG,"retrieved notes: "+notes );
            assertEquals( TESTUSER_NAME, name );
            assertEquals( TESTUSER_NOTES, notes );
            ++hits;
        }
        assertEquals( hits,1 );
        c.close();
    }

    public void tearDown() {
        contentResolver.delete( newPerson, null, null );
    }
}
