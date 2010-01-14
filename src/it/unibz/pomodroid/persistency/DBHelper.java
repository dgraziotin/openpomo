package it.unibz.pomodroid.persistency;

import it.unibz.pomodroid.exceptions.PomodroidException;
import java.io.File;
import android.content.Context;
import android.util.Log;
import com.db4o.Db4o;
import com.db4o.defragment.Defragment;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;

/**
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it>
 * 
 * This class manages the open source object database db40
 *
 */

public class DBHelper {

	private static ObjectContainer database;
	private Context context;

	/**
	 * Setting database parameters
	 * 
	 * @param context
	 */
	public DBHelper(Context context) {
		DBHelper.database = null;
		this.context = context;
	}

	/**
	 * Setting the class attribute
	 * 
	 * @param database
	 */
	public void setDatabase(ObjectContainer database) {
		DBHelper.database = database;
	}

	/**
	 * Getting the database
	 * 
	 * @return an object container
	 * @throws PomodroidException 
	 */
	public ObjectContainer getDatabase() throws PomodroidException {
		try {
			if (database == null || database.ext().isClosed())
				database = Db4o.openFile(dbConfig(), db4oDBFullPath(context));
			return database;
		} catch (Exception e) {
			Log.e(DBHelper.class.getName(), e.toString());
			throw new PomodroidException("ERROR in DBHelper.getDatabase():"+e.toString());
		}
	}

	/**
	 * 
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/**
	 * @param context
	 *            the context to set
	 */
	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 * Getting the database configuration
	 * 
	 * @return configuration
	 */
	private Configuration dbConfig() {
		Configuration configuration = Db4o.newConfiguration();
		configuration.lockDatabaseFile(false);
		configuration.objectClass(Activity.class).objectField("origin").indexed(true);
		configuration.objectClass(Activity.class).objectField("originId").indexed(true);
		configuration.objectClass(Activity.class).objectField("isTodoToday").indexed(true);
		configuration.objectClass(Activity.class).objectField("isDone").indexed(true);
		configuration.objectClass(Event.class).objectField("activity").indexed(true);
		return configuration;
	}

	/**
	 * 
	 * @param context
	 * @return database location
	 */
	public String db4oDBFullPath(Context context) {
		return context.getDir("data", 0) + "/" + "android.db4o";
	}
	
	public void defragment() throws PomodroidException{
		try {
			String db4oPath = db4oDBFullPath(context);
			String db4oPathBackup = db4oPath.concat(".backup");
			new File(db4oPathBackup).delete();
			Defragment.defrag((db4oDBFullPath(context)));
		} catch (Exception e) {
			throw new PomodroidException("Error in defragment: "+e.toString());
		}
	}

	/**
	 * Close database connection
	 */
	public void close() {
		if (database != null) {
			database.close();
			database = null;
		}
	}
	
	/**
	 * Forces a commit to the database
	 */
	public void commit() {
		if (database != null) {
			database.commit();
		}
	}

	/**
	 * Deleting the database
	 */
	public void deleteDatabase() {
		close();
		new File(db4oDBFullPath(context)).delete();
	}

	/**
	 * Creates a copy of the DB
	 * 
	 * @param path
	 * @throws PomodroidException
	 */
	public void backup(String path) throws PomodroidException {
		getDatabase().ext().backup(path);
	}

	/**
	 * Restores the DB
	 * 
	 * @param path
	 */
	public void restore(String path) {
		deleteDatabase();
		new File(path).renameTo(new File(db4oDBFullPath(context)));
		new File(path).delete();
	}

}
