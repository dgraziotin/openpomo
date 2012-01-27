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
package cc.task3.pomopro.models;

import android.util.Log;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;
import cc.task3.pomopro.exceptions.PomodroidException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * A class representing an activity. Each activity is described by its number of pomodoro (integer number),
 * received date (date), deadline (date), summary (string), description (string), origin (string i.e. trac),
 * origin id (integer that refers to its origin), priority (string), reporter (string), type (string),
 * todoToday (boolean), done (boolean) and endDate (date).
 *
 * @author Daniel Graziotin <d AT danielgraziotin DOT it>
 * @author Thomas Schievenin <thomas.schievenin@stud-inf.unibz.it>
 */

public class Activity {

    /**
     * Number of Pomodoros dedicated to the Activity
     */
    private int numberPomodoro;
    /**
     * Number of Interruptions during the Activity
     */
    private int numberInterruptions;
    /**
     * Date of Activity creation
     */
    private Date received;
    /**
     * Deadline of an Activity
     */
    private Date deadline;
    /**
     * Short summary or title of the Activity
     */
    private String summary;
    /**
     * Description of the Activity
     */
    private String description;
    /**
     * String that describes the origin of the Activity (name of Service, local)
     */
    private String origin;
    /**
     * Original id of the Activity as Issue in the Service
     */
    private int originId;
    /**
     * Priority of the issue
     */
    private String priority;
    /**
     * Name of the User reporting the Issue
     */
    private String reporter;
    /**
     * Type of Issue (bug, feature etc.)
     */
    private String type;
    /**
     * Represents if the Activity is in the TTS
     */
    private boolean todoToday;
    /**
     * True when the Activity is set as finished
     */
    private boolean done;
    /**
     * Stores the Date of Activity finished
     */
    private Date doneDate;

    /**
     * @param number_pomodoro number of pomodoro that has been runned
     * @param received        received date
     * @param deadline        deadline
     * @param summary         a briefly activity description
     * @param description     a more detalied description
     * @param origin          describes the activity origin
     * @param origin_id       id referring to the origin, it is impossible to have two activities with same origin and origin id
     * @param priority        priority of the activity
     * @param reporter        name of the reporter
     * @param type            activity type
     */
    public Activity(int numberPomodoro, int numberInterruptions, Date received,
                    Date deadline, String summary, String description, String origin,
                    int originId, String priority, String reporter, String type) {
        this.numberPomodoro = numberPomodoro;
        this.numberInterruptions = numberInterruptions;
        this.received = received;
        this.deadline = deadline;
        this.summary = summary;
        this.description = description;
        this.origin = origin;
        this.originId = originId;
        this.priority = priority;
        this.reporter = reporter;
        this.type = type;
        this.todoToday = false;
        this.done = false;
    }

    /**
     * Creates a quite empty Activity
     *
     * @param origin
     * @param originId
     */
    public Activity(String origin, int originId) {
        this.origin = origin;
        this.originId = originId;
    }

    /**
     * A helper method to update an existing Activity
     *
     * @param ac
     */
    public void update(Activity ac) {
        this.numberInterruptions = ac.getNumberInterruptions();
        this.numberPomodoro = ac.getNumberPomodoro();
        this.summary = ac.getSummary();
        this.todoToday = ac.isTodoToday();
        this.done = ac.isDone();
    }

    /**
     * @return the number of pomodoro
     */
    public int getNumberPomodoro() {
        return numberPomodoro;
    }

    /**
     * sets the number of pomodoro
     *
     * @param numberPomodoro
     */
    public void setNumberPomodoro(int numberPomodoro) {
        this.numberPomodoro = numberPomodoro;
    }

    /**
     * @return the number of pomodoro
     */
    public int getNumberInterruptions() {
        return numberInterruptions;
    }

    /**
     * sets the number of pomodoro
     *
     * @param numberPomodoro
     */
    public void setNumberInterruptions(int numberInterruptions) {
        this.numberInterruptions = numberInterruptions;
    }

    /**
     * @return when the activity has been received
     */
    public Date getReceived() {
        return received;
    }

    /**
     * received date to set
     *
     * @param received
     */
    public void setReceived(Date received) {
        this.received = received;
    }

    /**
     * @return the deadline
     */
    public Date getDeadline() {
        return deadline;
    }

    /**
     * @return the deadline as a string
     */
    public String getStringDeadline() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(this.deadline).toString();
    }

    /**
     * deadline to set
     *
     * @param deadline
     */
    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * description to set
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * origin to set
     *
     * @param origin
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * @return the origin id
     */
    public int getOriginId() {
        return originId;
    }

    /**
     * origin_id to set
     *
     * @param originId
     */
    public void setOriginId(int originId) {
        this.originId = originId;
    }

    /**
     * @return the reporter name
     */
    public String getReporter() {
        return reporter;
    }

    /**
     * reporter name to set
     *
     * @param reporter
     */
    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * the type to set
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }


    /**
     * summary to set
     *
     * @param summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }


    /**
     * @return the priority
     */
    public String getPriority() {
        return priority;
    }


    /**
     * the priority to set
     *
     * @param priority
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * @return todo today
     */
    public boolean isTodoToday() {
        return todoToday;
    }

    /**
     * the todoToday to set
     *
     * @param todoToday
     */
    public void setTodoToday(boolean todoToday) {
        this.todoToday = todoToday;
    }

    /**
     * @return is done
     */
    public boolean isDone() {
        return done;
    }

    /**
     * Set the activity as DONE and store the date
     */
    public void setDone() {
        this.done = true;
        setEndDate(new Date());
    }

    /**
     * @return end date
     */
    public Date getEndDate() {
        return doneDate;
    }

    /**
     * the date to set
     *
     * @param date
     */
    public void setEndDate(Date endDate) {
        this.doneDate = endDate;
    }

    /**
     * Set done to false
     */
    public void setUndone() {
        this.done = false;
    }

    /**
     * @return short summary description (for UI)
     */
    public String getShortSummary() {
        final int maxLength = 30;
        if (this.summary.length() > maxLength)
            return summary.substring(0, maxLength).replaceAll("\n", " ") + "...";
        return this.summary.replaceAll("\n", " ");
    }

    /**
     * Add one pomodoro
     */
    public void addOnePomodoro() {
        this.numberPomodoro++;
    }

    /**
     * Add one interruption
     */
    public void addOneInterruption() {
        this.numberInterruptions++;
    }


    /**
     * @param origin
     * @param originId
     * @param dbHelper
     * @return a specific activity
     * @throws cc.task3.pomopro.exceptions.PomodroidException
     *
     */
    public static boolean isPresent(final String origin, final int originId,
                                    DBHelper dbHelper) throws PomodroidException {
        List<Activity> activities;
        try {
            activities = dbHelper.getDatabase().query(
                    new Predicate<Activity>() {
                        private static final long serialVersionUID = 1L;

                        public boolean match(Activity activity) {
                            return activity.getOrigin().equals(origin)
                                    && activity.getOriginId() == originId;
                        }
                    });
            if (activities.isEmpty())
                return false;
            else
                return true;
        } catch (Exception e) {
            Log.e("Activity.isPresent()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.isPresent():"
                    + e.toString());
        }
    }

    /**
     * @param dbHelper
     * @return true if an activity is saved into the DB
     * @throws PomodroidException
     */
    public boolean save(DBHelper dbHelper) throws PomodroidException {
        try {
            if (!isPresent(this.getOrigin(), this.getOriginId(), dbHelper)) {
                dbHelper.getDatabase().store(this);
                return true;
            } else {
                return this.update(dbHelper);
            }
        } catch (Exception e) {
            Log.e("Activity.save(single)", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.save():"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

    /**
     * @param dbHelper
     * @return true if an activity is saved into the DB
     * @throws PomodroidException
     */
    private boolean update(DBHelper dbHelper) throws PomodroidException {
        ObjectSet<Activity> result = dbHelper.getDatabase().queryByExample(
                new Activity(getOrigin(), getOriginId()));
        Activity found = (Activity) result.next();
        found.update(this);
        try {
            dbHelper.getDatabase().store(found);
            return true;
        } catch (Exception e) {
            Log.e("Activity.save(single)", "Update Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.save(update):"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

    /**
     * Saves a list of activities
     *
     * @param Activities
     * @param dbHelper
     * @throws PomodroidException
     */
    public void save(List<Activity> Activities, DBHelper dbHelper)
            throws PomodroidException {
        try {
            for (Activity activity : Activities)
                activity.save(dbHelper);
        } catch (Exception e) {
            Log.e("Activity.save(List)", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.save(List):"
                    + e.toString());
        }
    }

    /**
     * Deletes an activity
     *
     * @param dbHelper
     * @throws PomodroidException
     */
    public void delete(DBHelper dbHelper) throws PomodroidException {
        ObjectSet<Activity> result;
        Activity toBeDeleted = null;
        try {
            toBeDeleted = Activity.get(this.getOrigin(), this.getOriginId(), dbHelper);
            result = dbHelper.getDatabase().queryByExample(toBeDeleted);
            Activity found = (Activity) result.next();
            dbHelper.getDatabase().delete(found);
        } catch (Exception e) {
            Log.e("Activity.delete()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.delete():"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

    /**
     * Deletes an activity
     *
     * @param dbHelper
     * @throws PomodroidException
     */
    public int getNumberInterruptions(DBHelper dbHelper) throws PomodroidException {
        List<Event> result = null;
        try {
            result = Event.getAllInterruptions(this, dbHelper);
        } catch (Exception e) {
            Log.e("Activity.getNumberInterruptions()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.getNumberInterruptions():"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
        if (result != null)
            return result.size();
        else
            return 0;
    }

    /**
     * Retrieves all activities
     *
     * @param dbHelper
     * @return
     * @throws PomodroidException
     */
    public static List<Activity> getAll(DBHelper dbHelper)
            throws PomodroidException {
        ObjectSet<Activity> result = null;
        try {
            result = dbHelper.getDatabase().queryByExample(Activity.class);
            if (result == null)
                Log.i("Activity.getAll()", "There are no activities stored in db");
        } catch (Exception e) {
            Log.e("Activity.getAll()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.getAll():" + e.toString());
        }
        return result;
    }

    /**
     * Delete all activities
     *
     * @param dbHelper
     * @return
     * @throws PomodroidException
     */

    public static boolean deleteAll(DBHelper dbHelper) throws PomodroidException {
        ObjectSet<Activity> activities = null;
        try {
            activities = dbHelper.getDatabase().query(new Predicate<Activity>() {
                private static final long serialVersionUID = 1L;

                public boolean match(Activity candidate) {
                    return true;
                }
            });
            while (activities.hasNext()) {
                dbHelper.getDatabase().delete(activities.next());
            }
            return true;
        } catch (Exception e) {
            Log.e("Activity.deleteAll()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.deleteAll():" + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

    /**
     * @param dbHelper
     * @return the number of activities
     * @throws PomodroidException
     */
    public static int getNumberActivities(DBHelper dbHelper)
            throws PomodroidException {
        List<Activity> activities = Activity.getAll(dbHelper);
        return ((activities == null) ? 0 : activities.size());
    }

    /**
     * @param origin
     * @param originId
     * @param dbHelper
     * @return a specific activity
     * @throws PomodroidException
     */
    public static Activity get(final String origin, final int originId,
                               DBHelper dbHelper) throws PomodroidException {
        List<Activity> activities = null;
        Activity result;
        try {
            activities = dbHelper.getDatabase().query(
                    new Predicate<Activity>() {
                        private static final long serialVersionUID = 1L;

                        public boolean match(Activity activity) {
                            return activity.getOrigin().equals(origin)
                                    && activity.getOriginId() == originId;
                        }
                    });
            result = activities.get(0);
        } catch (Exception e) {
            Log.e("Activity.getActivity()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.getActivity():"
                    + e.toString());
        }
        return result;
    }

    /**
     * @param dbHelper
     * @return to do today activities
     * @throws PomodroidException
     */
    public static List<Activity> getTodoToday(DBHelper dbHelper)
            throws PomodroidException {
        List<Activity> activities = null;
        Query query = null;
        try {
            query = dbHelper.getDatabase().query();
            query.constrain(Activity.class);
            query.descend("done").constrain(false);
            query.descend("todoToday").constrain(true);
            query.descend("deadline").orderAscending();
            activities = query.execute();
        } catch (Exception e) {
            Log.e("Activity.getTodoToday()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.getTodoToday():"
                    + e.toString());
        }
        return activities;
    }

    /**
     * @param dbHelper
     * @return all completed activities
     * @throws PomodroidException
     */
    public static List<Activity> getCompleted(DBHelper dbHelper)
            throws PomodroidException {
        List<Activity> activities = null;
        Query query = null;
        try {
            query = dbHelper.getDatabase().query();
            query.constrain(Activity.class);
            query.descend("done").constrain(true);
            query.descend("deadline").orderAscending();
            activities = query.execute();
        } catch (Exception e) {
            Log.e("Activity.getCompleted()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.getCompleted():"
                    + e.toString());
        }
        return activities;
    }

    /**
     * @param dbHelper
     * @return all uncompleted activities
     * @throws PomodroidException
     */
    public static List<Activity> getUncompleted(DBHelper dbHelper)
            throws PomodroidException {
        List<Activity> activities = null;
        Query query = null;
        try {
            query = dbHelper.getDatabase().query();
            query.constrain(Activity.class);
            query.descend("done").constrain(false);
            query.descend("deadline").orderAscending();
            activities = query.execute();
        } catch (Exception e) {
            Log.e("Activity.getUncompleted()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.getUncompleted():"
                    + e.toString());
        }
        return activities;
    }

    /**
     * Closes an activity
     *
     * @param dbHelper
     * @throws PomodroidException
     */
    public void close(DBHelper dbHelper) throws PomodroidException {
        try {
            this.setDone();
            this.setTodoToday(false);
            this.update(dbHelper);
        } catch (Exception e) {
            Log.e("Activity.close()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.close():"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

    /**
     * Returns all the activities belonging to a Service
     *
     * @param service
     * @param dbHelper
     * @return a list of Activities
     * @throws PomodroidException
     */
    public static List<Activity> getForService(final Service service,
                                               DBHelper dbHelper) throws PomodroidException {
        List<Activity> activities = null;
        try {
            activities = dbHelper.getDatabase().query(
                    new Predicate<Activity>() {
                        private static final long serialVersionUID = 1L;

                        public boolean match(Activity activity) {
                            return activity.getOrigin().equals(service.getUrl());
                        }
                    });
        } catch (Exception e) {
            Log.e("Activity.getActivity()", "Problem: " + e.toString());
            throw new PomodroidException("ERROR in Activity.getActivity():"
                    + e.toString());
        }
        return activities;
    }

    /**
     * @param activities
     * @return a list of their IDs
     * @throws PomodroidException
     */
    public static List<Integer> getOriginIDs(List<Activity> activities) throws PomodroidException {
        List<Integer> originIDs = new Vector<Integer>();
        for (Activity activity : activities)
            originIDs.add(activity.getOriginId());
        return originIDs;
    }


    /**
     * @param dbHelper
     * @return The last stored originId for local Activities
     * @throws PomodroidException
     */
    public static int getLastLocalId(DBHelper dbHelper)
            throws PomodroidException {

        List<Activity> activities = dbHelper.getDatabase().query(new Predicate<Activity>() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            public boolean match(Activity activity) {
                return activity.getOrigin().equals("local");
            }
        });

        if (activities == null || activities.size() == 0)
            return 0;

        int maxId = 0;
        for (Activity activity : activities) {
            if (activity.getOriginId() > maxId)
                maxId = activity.getOriginId();
        }

        return maxId;
    }
}