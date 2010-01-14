package it.unibz.pomodroid.services;

import it.unibz.pomodroid.exceptions.PomodroidException;

import java.net.URI;
import org.xmlrpc.android.XMLRPCClient;
import android.util.Log;

/**
 * @author Daniel Graziotin 4801 <daniel.graziotin@stud-inf.unibz.it>
 * @author Thomas Schievenin 5701 <thomas.schievenin@stud-inf.unibz.it> * 
 * 
 * A class that uses the remote procedure call protocol XML-RPC to retrieve information through the HTTP 
 * as a transport mechanism.
 * 
 */
public class XmlRpcClient {
	
	/**
	 * @param url
	 * @param method
	 * @param params
	 * @return a single object
	 * @throws PomodroidException 
	 */
	public static Object fetchSingleResult(String url, String method,
			Object[] params) throws PomodroidException {

		URI uri = URI.create(url);
		XMLRPCClient client = new XMLRPCClient(uri);

		Object result = null;

		try {
			result = client.call(method, params);
		} catch (Exception e) {
			Log.e("XML-RPC exception", e.toString());
			throw new PomodroidException("ERROR in XmlRpcClient.fetchSingleResult(): "+e.toString());
		}
		return result;
	}

	/**
	 * @param url
	 * @param method
	 * @param params
	 * @return one or more objects
	 * @throws PomodroidException 
	 */
	public static Object fetchMultiResults(String url, String method,
			Object[] params) throws PomodroidException {

		URI uri = URI.create(url);
		XMLRPCClient client = new XMLRPCClient(uri);

		Object[] result = null;

		try {
			result = (Object[]) client.call(method, params);
		} catch (Exception e) {
			Log.e("XML-RPC exception", e.toString());
			throw new PomodroidException("ERROR in XmlRpcClient.fetchSingleResults(): "+e.toString());
		}
		return result;
	}

	/**
	 * @param url
	 * @param username
	 * @param password
	 * @param method
	 * @param params
	 * @return signle object
	 * @throws PomodroidException 
	 */
	public static Object fetchSingleResult(String url, String username,
			String password, String method, Object[] params) throws PomodroidException {

		URI uri = URI.create(url);
		XMLRPCClient client = new XMLRPCClient(uri);
		if (url.contains("https")) {
			client.setBasicAuthentication(username, password);
		}
		Object result = null;

		try {
			result = client.call(method, params);
		} catch (Exception e) {
			Log.e("XML-RPC exception", e.toString());
			throw new PomodroidException("ERROR in XmlRpcClient.fetchSingleResult(): "+e.toString());
		}
		return result;
	}

	/**
	 * @param url
	 * @param username
	 * @param password
	 * @param method
	 * @param params
	 * @return one or more objects
	 * @throws PomodroidException 
	 */
	public static Object[] fetchMultiResults(String url, String username,
			String password, String method, Object[] params) throws PomodroidException {

		URI uri = URI.create(url);
		XMLRPCClient client = new XMLRPCClient(uri);
		if (url.contains("https")) {
			client.setBasicAuthentication(username, password);
		}
		Object[] result = null;

		try {
			result = (Object[]) client.call(method, params);
		} catch (Exception e) {
			Log.e("XML-RPC exception", e.toString());
			throw new PomodroidException("ERROR in XmlRpcClient.fetchSingleResults(): "+e.toString());
		}
		return result;
	}
}
