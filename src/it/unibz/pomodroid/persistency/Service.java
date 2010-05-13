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
package it.unibz.pomodroid.persistency;

import it.unibz.pomodroid.exceptions.PomodroidException;
import java.util.List;

import android.util.Log;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

/**
 * A class representing an extension of the Service class. Whit the
 * help of the open source object database db40 the Service is saved
 * into a local database.
 * @author Daniel Graziotin <daniel.graziotin@acm.org>
 * 
 */
public class Service extends it.unibz.pomodroid.models.Service{
	
	public Service(){
		super();
	}
	
	public Service(String name, String url, String type, String username, String password, boolean isAnonymousAccess){
		super(name,url,username,type,password,isAnonymousAccess);
	}
	
    public Service(String name, String url, String type, String username){
    	super(name, url, type, username);
	}
    
    /**
	 * Returns true if a Service with the given name is already
	 * present in the DB
	 * @param name
	 * @param dbHelper
	 * @return true if the Service is Present
	 * @throws PomodroidException
	 */
	public static boolean isPresent(final String name,
			DBHelper dbHelper) throws PomodroidException {
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
			throw new PomodroidException("ERROR in Service.isPresent():"
					+ e.toString());
		}
	}
	
	/**
	 * Returns true if a Service with a given URL is present
	 * in the DB
	 * @param url
	 * @param dbHelper
	 * @return a specific Service
	 * @throws PomodroidException
	 */
	public static boolean isPresentUrl(final String url,
			DBHelper dbHelper) throws PomodroidException {
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
			throw new PomodroidException("ERROR in Service.isPresent():"
					+ e.toString());
		}
	}
	
    
    /**
     * Saves a Service. It is also responsible for updating a Service if it is
     * already stored in the DB
	 * @param dbHelper
	 * @return true if a Service is saved into the DB
	 * @throws PomodroidException
	 */
	public boolean save(DBHelper dbHelper) throws PomodroidException {
		try {
			if (!isPresent(this.getName(), dbHelper)) {
				dbHelper.getDatabase().store(this);
				return true;
			} else {
				return this.update(dbHelper);
			}
		} catch (PomodroidException e) {
			Log.e("Service.save()", "Problem: " + e.toString());
			Log.e("Service.save()", "Problem: " + e.getMessage());
			throw new PomodroidException("ERROR in Service.save():"
					+ e.toString());
		} finally {
			dbHelper.commit();
		}
	}
	
	/**
	 * Updates an existing Service
	 * @param dbHelper
	 * @return true if a service is updated into the DB
	 * @throws PomodroidException
	 */
	private boolean update(DBHelper dbHelper) throws PomodroidException {
		Service oldService = Service.get(this.getName(), dbHelper);
		try {
			oldService.update(this);
			dbHelper.getDatabase().store(oldService);
			return true;
		} catch (Exception e) {
			Log.e("Service.save(update)", "Update Problem: " + e.toString());
			throw new PomodroidException("ERROR in Service.save(update):"
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
	 * @throws PomodroidException
	 */
	public static boolean deleteAll(DBHelper dbHelper)
			throws PomodroidException {
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
			throw new PomodroidException("ERROR in Service.deleteAll():"
					+ e.toString());
		}finally{
			dbHelper.commit();
		}
	}

	/**
	 * Returns all Services
	 * 
	 * @param dbHelper
	 * @return a list of Services
	 * @throws PomodroidException
	 */
	public static List<Service> getAll(DBHelper dbHelper)
			throws PomodroidException {
		ObjectSet<Service> result;
		try {
			result = dbHelper.getDatabase().queryByExample(Service.class);
			return result;
		} catch (Exception e) {
			throw new PomodroidException("ERROR in Service.getAll()"
					+ e.toString());
		}
	}
	
	/**
	 * Returns all active Services
	 * 
	 * @param dbHelper
	 * @return a list of Services
	 * @throws PomodroidException
	 */
	public static List<Service> getAllActive(DBHelper dbHelper)
			throws PomodroidException {
		List<Service> services = null;
		Query query= null;
		try {
				query = dbHelper.getDatabase().query();
				query.constrain(Service.class);
				query.descend("isActive").constrain(true);
				services = query.execute();
		} catch (Exception e) {
			Log.e("Service.getAllActive()", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in Service.getAllActive():"
					+ e.toString());
		}
		if(services!=null)
			Log.i("Active Services",""+services.size());
		return services;
	}
	
	/**
	 * Deletes a Service
	 * 
	 * @param dbHelper
	 * @throws PomodroidException
	 */
	public void delete(DBHelper dbHelper) throws PomodroidException {
		ObjectSet<Service> result;
		Service toBeDeleted = null;
		try {
			toBeDeleted = Service.get(this.getName(),dbHelper);
			result = dbHelper.getDatabase().queryByExample(toBeDeleted);
			Service found = (Service) result.next();
			dbHelper.getDatabase().delete(found);
		} catch (Exception e) {
			Log.e("Service.delete()", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in Service.delete():"
					+ e.toString());
		} finally {
			dbHelper.commit();
		}
	}
	
	/**
	 * Retrieves a Service from the DB, given its name
	 * @param name
	 * @param dbHelper
	 * @return a specific Service
	 * @throws PomodroidException
	 */
	public static Service get(final String name,
			DBHelper dbHelper) throws PomodroidException {
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
			if(services!=null && services.size()>0)
				result = services.get(0);
		} catch (Exception e) {
			Log.e("Service.getService()", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in Service.getService():"
					+ e.toString());
		}
		return result;
	}
}
