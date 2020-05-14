/*
    Author: Pavel Yadlouski (xyadlo00)
            Oleksii Korniienko (xkorni02)

    File: src/functional/Line.java
    Date: 04.2020
 */

package src.functional;

import java.util.HashMap;
import java.util.List;

public interface Line extends Drawable {
	
	static Line defaultLine(String id) {
		return new MyLine(id);
	}

	static Line defaultLine(Line newLine){
		return new MyLine(newLine);
	}

	HashMap<String, String> getStreetsTypes();

	HashMap<String, Integer> getStopsTimes();

	HashMap<String, Integer> getStopsFlags();


	void addStreetType(String street_name, String street_type);

	void addStopsTimes(String stop_name, Integer stop_times, Integer delay);

	void addStopsFlags(String stop_name, Integer stop_flag);


	String getId();

	boolean addStreet(Street street);
	
	boolean addStop(Stop stop);
	
	java.util.List<java.util.AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute();

	List<Stop> getStops();

	List<Street> getStreets();

	Street getBlockedStreet();

	void setBlockedStreet(Street new_blocked_street);

	List<Street> getTempNewStreet();

	List<Street> getPaintedStreet();

	List<Stop> getTempNewStops();

	void setNewStreets(List<Street> new_streets);

	void setNewStops(List<Stop> new_stops);
}
