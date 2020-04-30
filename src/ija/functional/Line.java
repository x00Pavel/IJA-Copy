package ija.functional;

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

	public String getId();

	public boolean addStreet(Street street);
	
	public boolean addStop(Stop stop);
	
	public java.util.List<java.util.AbstractMap.SimpleImmutableEntry<Street, Stop>> getRoute();

	public List<Stop> getStops();

	public List<Street> getStreets();
}
