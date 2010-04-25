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
	
	public Service(String name, String url, String type, String username, String password, boolean isAnonymousAccess){
		super(name,url,username,type,password,isAnonymousAccess);
	}
	
    public Service(String name, String url, String type, String username){
    	super(name, url, type, username);
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
	 * 
	 * @param origin
	 * @param originId
	 * @param dbHelper
	 * @return a specific Service
	 * @throws PomodroidException
	 */
	public static Service get(final String name,
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
				return null;
			else
				return services.get(0);
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
			if (!isPresent(this.getName(), dbHelper)) {
				dbHelper.getDatabase().store(this);
				return true;
			} else {
				return this.update(dbHelper);
			}
		} catch (Exception e) {
			Log.e("Service.save()", "Problem: " + e.toString());
			throw new PomodroidException("ERROR in Service.save():"
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
		ObjectSet<Service> result = dbHelper.getDatabase().queryByExample(
				new Service(this.getName(), this.getUrl(), this.getType(), this.getUsername()));
		Service found = (Service) result.next();
		found.delete(dbHelper);
		try {
			dbHelper.getDatabase().store(this);
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
	 * Deletes an activity
	 * 
	 * @param dbHelper
	 * @throws PomodroidException
	 */
	public void delete(DBHelper dbHelper) throws PomodroidException {
		ObjectSet<Service> result;
		Service toBeDeleted = null;
		try {
			toBeDeleted = Service.getService(this.getName(),dbHelper);
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
}
