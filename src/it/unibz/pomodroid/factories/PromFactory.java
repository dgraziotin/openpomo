package it.unibz.pomodroid.factories;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
	

	
	/**
	 * Returns a Zip file containing all events and locations stored on the
	 * application database.
	 * 
	 * @param events
	 * @param locations
	 * @return
	 * @throws IOException
	 */
	public byte[] createZip(List<Event> events, User user) throws IOException {
		byte[] ret = null;
		
		ByteArrayOutputStream dest = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new CheckedOutputStream(dest, new Adler32())));

		int prefix = 0;
		
		for(Event event : events) {
			ZipEntry entry = new ZipEntry(prefix + ".ini");
			out.putNextEntry(entry);
			byte[] buffer = PromFactory.createIniEntry(event, user).getBytes();
			out.write( buffer );
			out.flush();
			out.closeEntry();				
			prefix++;
						
		}
		
		Log.i("PromFactory.createZip", "Events processed: " + events.size());
		Log.i("PromFactory.createZip", "Zip file contains " + prefix + " files");
		
		out.finish();
		out.flush();
		ret = dest.toByteArray();
		out.close();
		return ret;
	}
}
