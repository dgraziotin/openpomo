/**
 * This file is part of OpenPomo.
 *
 *   OpenPomo is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   OpenPomo is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with OpenPomo.  If not, see <http://www.gnu.org/licenses/>.
 */
package cc.task3.openpomopro.models;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import cc.task3.openpomopro.exceptions.OpenPomoException;


import java.util.Date;
import java.util.List;

/**
 * A class representing an event. Each event has its type (string), value (string), date (date) and reference to
 * its activity.
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 */
public class Event {

    private String type;
    private String value;
    private Date timestamp;
    private Activity activity;
    private long atPomodoroValue;


    /**
     * @param type      event type
     * @param value     event value
     * @param timestamp date
     * @param activity  reference to its activity
     */
    public Event(String type, String value, Date timestamp,
                 Activity activity, long atPomodoroValue) {
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
        this.activity = activity;
        this.atPomodoroValue = atPomodoroValue;
    }

    /**
     * @param type      event type
     * @param value     event value
     * @param timestamp date
     * @param activity  reference to its activity
     */
    public Event(String type, String value, Date timestamp,
                 Activity activity) {
        this.type = type;
        this.value = value;
        this.timestamp = timestamp;
        this.activity = activity;
        this.atPomodoroValue = -1;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the activity
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * @param activity the activity to set
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * @return number of pomodoro
     */
    public long getAtPomodoroValue() {
        return atPomodoroValue;
    }

    /**
     * The number of pomodoro to set
     *
     * @param atPomodoroValue
     */
    public void setAtPomodoroValue(long atPomodoroValue) {
        this.atPomodoroValue = atPomodoroValue;
    }

    /**
     * Save an event into the DB
     *
     * @param dbHelper
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     *
     */
    public void save(DBHelper dbHelper) throws OpenPomoException {
        try {
            dbHelper.getDatabase().store(this);
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Event.save()" + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

    /**
     * Delete all events
     *
     * @param dbHelper
     * @return
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */

    public static boolean deleteAll(DBHelper dbHelper)
            throws OpenPomoException {
        ObjectSet<Event> events = null;
        try {
            events = dbHelper.getDatabase().query(new Predicate<Event>() {
                private static final long serialVersionUID = 1L;

                public boolean match(Event candidate) {
                    return true;
                }
            });
            while (events.hasNext()) {
                dbHelper.getDatabase().delete(events.next());
            }
            return true;
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Event.deleteAll():"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

    /**
     * Returns all events
     *
     * @param dbHelper
     * @return a list of events
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static List<Event> getAll(DBHelper dbHelper)
            throws OpenPomoException {
        ObjectSet<Event> result;
        try {
            result = dbHelper.getDatabase().queryByExample(Event.class);
            return result;
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Event.getAll()"
                    + e.toString());
        }
    }

    /**
     * Gets all events
     *
     * @param activity
     * @param dbHelper
     * @return
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static List<Event> getAll(final Activity activity, DBHelper dbHelper)
            throws OpenPomoException {
        ObjectSet<Event> result;
        try {
            result = dbHelper.getDatabase().query(new Predicate<Event>() {
                private static final long serialVersionUID = 1L;

                public boolean match(Event event) {
                    return (event.getActivity().getOrigin().equals(
                            activity.getOrigin()) && event.getActivity()
                            .getOriginId() == activity.getOriginId());
                }
            });
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Event.getAll()"
                    + e.toString());
        }
        return result;
    }

    /**
     * Gets all events
     *
     * @param activity
     * @param dbHelper
     * @return
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static List<Event> getAllInterruptions(final Activity activity, DBHelper dbHelper)
            throws OpenPomoException {
        ObjectSet<Event> result;
        try {
            result = dbHelper.getDatabase().query(new Predicate<Event>() {
                private static final long serialVersionUID = 1L;

                public boolean match(Event event) {
                    return (event.getActivity().getOrigin().equals(
                            activity.getOrigin()) && event.getActivity()
                            .getOriginId() == activity.getOriginId() && event.getType().equals("stop"));
                }
            });
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Event.getAll()"
                    + e.toString());
        }
        return result;
    }

    /**
     * Gets all events
     *
     * @param activity
     * @param dbHelper
     * @return
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static List<Event> getAllInterruptions(DBHelper dbHelper)
            throws OpenPomoException {
        ObjectSet<Event> result;
        try {
            result = dbHelper.getDatabase().query(new Predicate<Event>() {
                private static final long serialVersionUID = 1L;

                public boolean match(Event event) {
                    return (event.getType().equals("pomodoro") && event.getValue().equals("stop"));
                }
            });
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Event.getAll()"
                    + e.toString());
        }
        return result;
    }

    /**
     * Gets all events
     *
     * @param activity
     * @param dbHelper
     * @return
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static List<Event> getAllStartedPomodoros(DBHelper dbHelper)
            throws OpenPomoException {
        ObjectSet<Event> result;
        try {
            result = dbHelper.getDatabase().query(new Predicate<Event>() {
                private static final long serialVersionUID = 1L;

                public boolean match(Event event) {
                    return (event.getType().equals("pomodoro") && event.getValue().equals("start"));
                }
            });
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Event.getAll()"
                    + e.toString());
        }
        return result;
    }

    /**
     * Gets all events
     *
     * @param activity
     * @param dbHelper
     * @return
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static List<Event> getAllFinishedPomodoros(DBHelper dbHelper)
            throws OpenPomoException {
        ObjectSet<Event> result;
        try {
            result = dbHelper.getDatabase().query(new Predicate<Event>() {
                private static final long serialVersionUID = 1L;

                public boolean match(Event event) {
                    return (event.getType().equals("pomodoro") && event.getValue().equals("finish"));
                }
            });
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Event.getAll()"
                    + e.toString());
        }
        return result;
    }


    /**
     * Deletes all events
     *
     * @param activity
     * @param dbHelper
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static void delete(final Activity activity, DBHelper dbHelper)
            throws OpenPomoException {
        try {
            List<Event> eventsForActivity = Event.getAll(activity, dbHelper);

            for (Event ev : eventsForActivity) {
                dbHelper.getDatabase().delete(ev);
            }
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Event.delete()"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

}
