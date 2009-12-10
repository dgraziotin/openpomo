package it.unibz.pomodroid.factories;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.User;
import android.util.Log;

public class PromFactory {

	private static final int EVENT_PLUGIN_ID = 14;
	private static final int LOCATION_PLUGIN_ID = 15;
	private static final int ANDROID_APPLICATION_ID = 1000;
	public static final String PROM_DATE_FORMAT = "yyyyMMddHHmmss";
	
	public static void promify(List<Event> events, User user) {
	List<String> iniEvents = new ArrayList<String>();
		for (Event event : events) {
			iniEvents.add(createIniEntry(event, user));
			Log.i("EVENT: ", createIniEntry(event, user));
		}
	}

	/**
	 * Creates the content of an INI PROM event file.
	 * 
	 * @param entity
	 * @param time
	 * @param users
	 * @param latitude
	 * @param longitude
	 * @param trip_id
	 * @return
	 */
	public static String createIniEntry(Event event, User user) {
		String entity = "/origin=" + event.getActivity().getOrigin() + "/id="
				+ event.getActivity().getOriginId();
		SimpleDateFormat sdf;
		String datetime = null;
		try {
			sdf = new SimpleDateFormat(PROM_DATE_FORMAT);
			datetime = sdf.format(event.getTimestamp());
		} catch (Exception e) {
			Log.e("SimpleDateFormat", e.toString());
		}
		String ini = "[event]\n" + "plugin = " + LOCATION_PLUGIN_ID + "\n"
				+ "application = " + ANDROID_APPLICATION_ID + "\n"
				+ "entity = " + entity + "\n" + "datetime = " + datetime + "\n"
				+ "users = " + user.getTracUsername() + "\n" + "\n"
				+ "[properties]\n" + "eventType = " + event.getType() + "\n"
				+ "eventValue = " + event.getValue() + "\n";
		// TODO: add Pomodoro when it's ready.

		return ini;
	}
}
