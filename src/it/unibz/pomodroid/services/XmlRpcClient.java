package it.unibz.pomodroid.services;

import java.net.URI;
import org.xmlrpc.android.XMLRPCClient;
import android.util.Log;

public class XmlRpcClient {
	public static Object fetchSingleResult(String url, String method,
			Object[] params) throws Exception {

		URI uri = URI.create(url);
		XMLRPCClient client = new XMLRPCClient(uri);

		Object result = null;

		try {
			result = client.call(method, params);
		} catch (Exception e) {
			Log.e("XML-RPC exception", e.toString());
			throw e;
		}
		return result;
	}

	public static Object fetchMultiResults(String url, String method,
			Object[] params) {

		URI uri = URI.create(url);
		XMLRPCClient client = new XMLRPCClient(uri);

		Object[] result = null;

		try {
			result = (Object[]) client.call(method, params);
		} catch (Exception e) {
			Log.e("XML-RPC exception", e.toString());
		}
		return result;
	}

	public static Object fetchSingleResult(String url, String username,
			String password, String method, Object[] params) {

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
		}
		return result;
	}

	public static Object[] fetchMultiResults(String url, String username,
			String password, String method, Object[] params) {

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
		}
		return result;
	}
}
