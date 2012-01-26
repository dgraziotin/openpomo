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
 *   along with Pomodroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibz.pomodroid.models;

import android.content.Context;
import android.util.Log;
import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.config.Configuration;
import com.db4o.defragment.Defragment;
import it.unibz.pomodroid.exceptions.PomodroidException;
import it.unibz.pomodroid.models.*;


import java.io.File;

/**
 * This class manages the open source object database db40
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
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
     * @throws it.unibz.pomodroid.exceptions.PomodroidException
     *
     */
    @SuppressWarnings("deprecation")
    public ObjectContainer getDatabase() throws PomodroidException {
        try {
            if (database == null || database.ext().isClosed())
                database = Db4o.openFile(dbConfig(), db4oDBFullPath(context));
            return database;
        } catch (Exception e) {
            Log.e(DBHelper.class.getName(), e.toString());
            throw new PomodroidException("ERROR in DBHelper.getDatabase():" + e.toString());
        }
    }

    /**
     * @return the context
     */
    public Context getContext() {
        return context;
    }

    /**
     * the context to set
     *
     * @param context
     */
    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Getting the database configuration
     *
     * @return configuration
     */
    @SuppressWarnings("deprecation")
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
     * @param context
     * @return database location
     */
    public String db4oDBFullPath(Context context) {
        return context.getDir("data", 0) + "/" + "android.db4o";
    }

    public void defragment() throws PomodroidException {
        try {
            String db4oPath = db4oDBFullPath(context);
            String db4oPathBackup = db4oPath.concat(".backup");
            new File(db4oPathBackup).delete();
            Defragment.defrag((db4oDBFullPath(context)));
        } catch (Exception e) {
            throw new PomodroidException("Error in defragment: " + e.toString());
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
     * @throws it.unibz.pomodroid.exceptions.PomodroidException
     *
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
