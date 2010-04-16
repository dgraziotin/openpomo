package it.unibz.pomodroid.persistency;

import it.unibz.pomodroid.exceptions.PomodroidException;
import java.util.List;

import android.util.Log;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

public class Service extends it.unibz.pomodroid.models.Service{
	public Service(){
		super();
	}
	
	public Service(String name, String url, String username, String password, boolean isAnonymousAccess){
		super(name,url,username,password,isAnonymousAccess);
	}
	
    public Service(String name, String url, String username){
    	super(name, url, username);
	}
    
    /**
	 * 
	 * @param origin
	 * @param originId
	 * @param dbHelper
	 * @return a specific Service
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
	 * @param dbHelper
	 * @return true if an Service is saved into the DB
	 * @throws PomodroidException
	 */
	public boolean save(DBHelper dbHelper) throws PomodroidException {
		try {
			if (!isPresent(this.getName(),dbHelper)) {

				dbHelper.getDatabase().store(this);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			Log.e("Service.save(single)", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in Service.save():"
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
	 * @param origin
	 * @param originId
	 * @param dbHelper
	 * @return a specific Service
	 * @throws PomodroidException
	 */
	public static Service getService(final String name,
			DBHelper dbHelper) throws PomodroidException {
		List<Service> services = null;
		Service result;
		try {
			services = dbHelper.getDatabase().query(
					new Predicate<Service>() {
						private static final long serialVersionUID = 1L;

						public boolean match(Service service) {
							return service.getName().equals(name);
						}
					});
			result = services.get(0);
		} catch (Exception e) {
			Log.e("Service.getService()", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in Service.getService():"
					+ e.toString());
		}
		return result;
	}

	

	/**
	 * Deletes all services
	 * 
	 * @param Service
	 * @param dbHelper
	 * @throws PomodroidException
	 * 
	 */
	public static void delete(DBHelper dbHelper)
			throws PomodroidException {
		try {
			List<Service> services = Service.getAll(dbHelper);
			
			for (Service service : services) {
				dbHelper.getDatabase().delete(service);
			}
		} catch (Exception e) {
			throw new PomodroidException("ERROR in Service.delete()"
					+ e.toString());
		}finally{
			dbHelper.commit();
		}
	}
}
