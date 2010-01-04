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

import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.Event;
import it.unibz.pomodroid.persistency.User;
import android.util.Log;
import it.unibz.pomodroid.exceptions.PomodroidException;

/**
 * @author Thomas Schievenin
 * 
 *         A class that saves all information about an activity it into PROM.
 *         Each activity taken from our internal database.
 * 
 */
public class PromFactory {

	private static final int LOCATION_PLUGIN_ID = 15;
	private static final int ANDROID_APPLICATION_ID = 1000;
	public static final String PROM_DATE_FORMAT = "yyyyMMddHHmmss";

	public static void promify(List<Event> events, User user)
			throws PomodroidException {
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
	public static String createIniEntry(Event event, User user)
			throws PomodroidException {

		String entity = "/" + event.getType() + "=" + event.getValue();
		final Activity activity = event.getActivity();

		SimpleDateFormat sdf;
		String datetime = null;
		String ini = null;
		try {
			sdf = new SimpleDateFormat(PROM_DATE_FORMAT);
			datetime = sdf.format(event.getTimestamp());

			ini = "[event]\n" + "plugin = " + LOCATION_PLUGIN_ID + "\n"
					+ "application = " + ANDROID_APPLICATION_ID + "\n"
					+ "entity = " + entity + "\n" 
					+ "datetime = " + datetime + "\n" 
					+ "users = " + user.getTracUsername() + "\n" + "\n"
					+ "[properties]\n"  
					+ "pomodoro = " + activity.getNumberPomodoro() + "\n"
					+ "pomodoroDuration = " + event.getAtPomodoroValue() + "\n"
					+ "repositoryurl = " + activity.getOrigin() + "\n"
					+ "issueid = " + activity.getOriginId() + "\n";

		} catch (Exception e) {
			Log.e("PromFactory.createIniEntry", e.toString());
			throw new PomodroidException(
					"ERROR in PromFactory.createIniEntry():" + e.toString());
		}
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
	 * @throws PomodroidException
	 */
	public byte[] createZip(List<Event> events, User user)
			throws PomodroidException {
		
		byte[] ret = null;

		ByteArrayOutputStream dest = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
				new CheckedOutputStream(dest, new Adler32())));

		int prefix = 0;
		try {
			for (Event event : events) {
				ZipEntry entry = new ZipEntry(prefix + ".ini");
				out.putNextEntry(entry);
				byte[] buffer = PromFactory.createIniEntry(event, user)
						.getBytes();
				out.write(buffer);
				out.flush();
				out.closeEntry();
				prefix++;

			}

			Log
					.i("PromFactory.createZip", "Events processed: "
							+ events.size());
			Log.i("PromFactory.createZip", "Zip file contains " + prefix
					+ " files");

			out.finish();
			out.flush();
			ret = dest.toByteArray();
			out.close();
		} catch (Exception e) {
			Log.i("PromFactory.createZip", "ERROR: " +e.toString());
			throw new PomodroidException("ERROR in PromFactory.createZip():"+ e.toString());
		} 
		return ret;
	}
}
