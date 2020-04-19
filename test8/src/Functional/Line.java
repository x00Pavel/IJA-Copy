package Functional;

import java.util.List;

public interface Line {
	
	public static Line defaultLine(String id) {
		Line line = new MyLine(id);
		return line;
	}
	
	public boolean addStreet(Street street);
	
	public boolean addStop(Stop stop);
	
	public java.util.List<java.util.AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute();

	public List<Stop> getStops();

	public List<Street> getStreets();
}
