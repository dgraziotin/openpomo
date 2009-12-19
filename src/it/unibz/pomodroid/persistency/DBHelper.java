package it.unibz.pomodroid.persistency;

import java.io.File;

import android.content.Context;
import android.util.Log;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;

public class DBHelper {

	private static ObjectContainer database;
	/**
	 * @return the context
	 */
	public static Context getContext() {
		return context;
	}

	private static Context context;

	

	public static ObjectContainer getDatabase() {
		try {
			if (database == null || database.ext().isClosed())
				database = Db4o.openFile(dbConfig(), db4oDBFullPath(context));
			return database;
		} catch (Exception e) {
			Log.e(DBHelper.class.getName(), e.toString());
			return null;
		}
	}

	
	/**
	 * @param context
	 *            the context to set
	 */
	public static void setContext(Context context) {
		DBHelper.context = context;
	}

	private static Configuration dbConfig() {
		Configuration configuration = Db4o.newConfiguration();
		return configuration;
	}

	/**
	 * @param context
	 * @return
	 */
	public static String db4oDBFullPath(Context context) {
		return context.getDir("data", 0) + "/" + "android.db4o";
	}

	/**
	 * Close database connection
	 */
	public static void close() {
		if (database != null) {
			database.close();
			database = null;
		}
	}

	public static void deleteDatabase() {
		close();
		new File(db4oDBFullPath(context)).delete();
	}

	public static void backup(String path) {
		getDatabase().ext().backup(path);
	}

	public static void restore(String path) {
		deleteDatabase();
		new File(path).renameTo(new File(db4oDBFullPath(context)));
		new File(path).delete();
	}

}
