/**
 * File: ija/src/functional/MyLine.java
 * 
 * Author: Pavel Yadlouski (xyadlo00)
 *         Oleksii Korniienko (xkorni02)
 * 
 * Date: 04.2020
 * 
 * Description: Implementation of MyLine object with its functionality
 */


package src.functional;

import java.util.HashMap;
import java.util.List;

public interface Line extends Drawable {
	
	public static Line defaultLine(String id) {
		Line line = new MyLine(id);
		return line;
	}

	public static Line defaultLine(Line newLine){
		Line line = new MyLine(newLine);
		return line;
	}

	public HashMap<String, String> getStreetsTypes();

	public HashMap<String, Integer> getStopsTimes();

	public HashMap<String, Integer> getStopsFlags();

//	public HashMap<String, Integer> getStopsDelays();

	public void addStreetType(String street_name, String street_type);

	public void addStopsTimes(String stop_name, Integer stop_times, Integer delay);

	public void addStopsFlags(String stop_name, Integer stop_flag);

//	public void addStopsDelays(String stop_name, Integer stop_delay);

	public String getId();

	public boolean addStreet(Street street);
	
	public boolean addStop(Stop stop);
	
	public java.util.List<java.util.AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute();

	public List<Stop> getStops();

	public List<Street> getStreets();
}
