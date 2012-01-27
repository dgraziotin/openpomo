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

import java.util.List;

import android.util.Log;

import cc.task3.openpomopro.exceptions.OpenPomoException;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

/**
 * A class representing a Service.
 *
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 */

public class Service {
    /**
     * User given name for the Service
     */
    private String name;
    /**
     * URL for the Service
     */
    private String url;
    /**
     * Service username
     */
    private String username;
    /**
     * Service password (if needed)
     */
    private String password;
    /**
     * Type of Service (Trac, BugZilla, ..)
     */
    private String type;
    /**
     * True if the Service does not require HTTP BASIC AUTH
     */
    private boolean isAnonymousAccess;
    /**
     * True if the User wants issues to be searched from it
     */
    private boolean isActive;

    /**
     * Default Constructor. Sets all fields to null
     */
    public Service() {
        this.name = null;
        this.url = null;
        this.username = null;
        this.password = null;
        this.isAnonymousAccess = false;
        this.setType(null);
        this.isActive = true;
    }

    /**
     * @param name
     * @param url
     * @param type
     * @param username
     * @param password
     * @param isAnonymousAccess
     */
    public Service(String name, String url, String type, String username, String password, boolean isAnonymousAccess) {
        this.name = name;
        this.url = url;
        this.setType(type);
        this.username = username;
        this.password = password;
        this.isAnonymousAccess = isAnonymousAccess;
        this.isActive = true;
    }

    /**
     * @param name
     * @param url
     * @param type
     * @param username
     */
    public Service(String name, String url, String type, String username) {
        this.name = name;
        this.url = url;
        this.setType(type);
        this.username = username;
        this.password = "";
        this.isAnonymousAccess = true;
        this.isActive = true;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the isAnonymousAccess
     */
    public boolean isAnonymousAccess() {
        return isAnonymousAccess;
    }

    /**
     * @param isAnonymousAccess the isAnonymousAccess to set
     */
    public void setAnonymousAccess(boolean isAnonymousAccess) {
        this.isAnonymousAccess = isAnonymousAccess;
    }

    /**
     * @param isActive
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Helper method for updating an existing Service
     *
     * @param service
     */
    public void update(Service service) {
        this.name = service.getName();
        this.url = service.getUrl();
        this.type = service.getType();
        this.username = service.getUsername();
        this.password = service.getPassword();
        this.isAnonymousAccess = service.isAnonymousAccess();
        this.isActive = service.isActive();
    }

    /**
     * Returns true if a Service with the given name is already
     * present in the DB
     *
     * @param name
     * @param dbHelper
     * @return true if the Service is Present
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static boolean isPresent(final String name,
                                    DBHelper dbHelper) throws OpenPomoException {
        List<Service> services;
        try {
            services = dbHelper.getDatabase().query(
                    new Predicate<Service>() {
                        private static final long serialVersionUID = 1L;

                        public boolean match(Service service) {
                            return service.getName().equals(name);
                        }
                    });
            if (services.isEmpty())
                return false;
            else
                return true;
        } catch (Exception e) {
            Log.e("Service.isPresent()", "Problem: " + e.toString());
            throw new OpenPomoException("ERROR in Service.isPresent():"
                    + e.toString());
        }
    }

    /**
     * Returns true if a Service with a given URL is present
     * in the DB
     *
     * @param url
     * @param dbHelper
     * @return a specific Service
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static boolean isPresentUrl(final String url,
                                       DBHelper dbHelper) throws OpenPomoException {
        List<Service> services;
        try {
            services = dbHelper.getDatabase().query(
                    new Predicate<Service>() {
                        private static final long serialVersionUID = 1L;

                        public boolean match(Service service) {
                            return service.getUrl().equals(url);
                        }
                    });
            if (services.isEmpty())
                return false;
            else
                return true;
        } catch (Exception e) {
            Log.e("Service.isPresentUrl()", "Problem: " + e.toString());
            throw new OpenPomoException("ERROR in Service.isPresent():"
                    + e.toString());
        }
    }


    /**
     * Saves a Service. It is also responsible for updating a Service if it is
     * already stored in the DB
     *
     * @param dbHelper
     * @return true if a Service is saved into the DB
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public boolean save(DBHelper dbHelper) throws OpenPomoException {
        try {
            if (!isPresent(this.getName(), dbHelper)) {
                dbHelper.getDatabase().store(this);
                return true;
            } else {
                return this.update(dbHelper);
            }
        } catch (OpenPomoException e) {
            Log.e("Service.save()", "Problem: " + e.toString());
            Log.e("Service.save()", "Problem: " + e.getMessage());
            throw new OpenPomoException("ERROR in Service.save():"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

    /**
     * Updates an existing Service
     *
     * @param dbHelper
     * @return true if a service is updated into the DB
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    private boolean update(DBHelper dbHelper) throws OpenPomoException {
        Service oldService = Service.get(this.getName(), dbHelper);
        try {
            oldService.update(this);
            dbHelper.getDatabase().store(oldService);
            return true;
        } catch (Exception e) {
            Log.e("Service.save(update)", "Update Problem: " + e.toString());
            throw new OpenPomoException("ERROR in Service.save(update):"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
    }


    /**
     * Delete all Services
     *
     * @param dbHelper
     * @return
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static boolean deleteAll(DBHelper dbHelper)
            throws OpenPomoException {
        ObjectSet<Service> services = null;
        try {
            services = dbHelper.getDatabase().query(new Predicate<Service>() {
                private static final long serialVersionUID = 1L;

                @Override
                public boolean match(Service arg0) {
                    // TODO Auto-generated method stub
                    return false;
                }
            });
            while (services.hasNext()) {
                dbHelper.getDatabase().delete(services.next());
            }
            return true;
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Service.deleteAll():"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

    /**
     * Returns all Services
     *
     * @param dbHelper
     * @return a list of Services
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static List<Service> getAll(DBHelper dbHelper)
            throws OpenPomoException {
        ObjectSet<Service> result;
        try {
            result = dbHelper.getDatabase().queryByExample(Service.class);
            return result;
        } catch (Exception e) {
            throw new OpenPomoException("ERROR in Service.getAll()"
                    + e.toString());
        }
    }

    /**
     * Returns all active Services
     *
     * @param dbHelper
     * @return a list of Services
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static List<Service> getAllActive(DBHelper dbHelper)
            throws OpenPomoException {
        List<Service> services = null;
        Query query = null;
        try {
            query = dbHelper.getDatabase().query();
            query.constrain(Service.class);
            query.descend("isActive").constrain(true);
            services = query.execute();
        } catch (Exception e) {
            Log.e("Service.getAllActive()", "Problem: " + e.toString());
            throw new OpenPomoException("ERROR in Service.getAllActive():"
                    + e.toString());
        }
        if (services != null)
            Log.i("Active Services", "" + services.size());
        return services;
    }

    /**
     * Deletes a Service
     *
     * @param dbHelper
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public void delete(DBHelper dbHelper) throws OpenPomoException {
        ObjectSet<Service> result;
        Service toBeDeleted = null;
        try {
            toBeDeleted = Service.get(this.getName(), dbHelper);
            result = dbHelper.getDatabase().queryByExample(toBeDeleted);
            Service found = (Service) result.next();
            dbHelper.getDatabase().delete(found);
        } catch (Exception e) {
            Log.e("Service.delete()", "Problem: " + e.toString());
            throw new OpenPomoException("ERROR in Service.delete():"
                    + e.toString());
        } finally {
            dbHelper.commit();
        }
    }

    /**
     * Retrieves a Service from the DB, given its name
     *
     * @param name
     * @param dbHelper
     * @return a specific Service
     * @throws cc.task3.openpomopro.exceptions.OpenPomoException
     */
    public static Service get(final String name,
                              DBHelper dbHelper) throws OpenPomoException {
        List<Service> services = null;
        Service result = null;
        try {
            services = dbHelper.getDatabase().query(
                    new Predicate<Service>() {
                        private static final long serialVersionUID = 1L;

                        public boolean match(Service service) {
                            return service.getName().equals(name);
                        }
                    });
            if (services != null && services.size() > 0)
                result = services.get(0);
        } catch (Exception e) {
            Log.e("Service.getService()", "Problem: " + e.toString());
            throw new OpenPomoException("ERROR in Service.getService():"
                    + e.toString());
        }
        return result;
    }

}