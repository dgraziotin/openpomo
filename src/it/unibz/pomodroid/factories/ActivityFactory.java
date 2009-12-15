package it.unibz.pomodroid.factories;

import it.unibz.pomodroid.persistency.Activity;
import it.unibz.pomodroid.persistency.DBHelper;
import java.util.Date;
import java.util.HashMap;

public class ActivityFactory {

	public static void produce (int id, Date dueDate, HashMap<String,String> attributes, DBHelper dbHelper){
		Activity activity = new Activity(0, new Date(), dueDate, attributes
				.get("summary").toString(), attributes.get("description")
				.toString(), "PROM", id, attributes.get("priority").toString(),
				attributes.get("reporter").toString(), attributes.get("type")
						.toString());
		 activity.save(dbHelper);
	}
	
}
