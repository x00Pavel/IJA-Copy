package Functional;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class MyStreet implements Street {

	private String myStreetName;
	private List<Coordinate> coordinates = new ArrayList<>();
	private List<Stop> stops = new ArrayList<>();
	private List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = new ArrayList<AbstractMap.SimpleImmutableEntry<Stop, Integer>> ();

	public MyStreet(String name, Coordinate[] arrayOfCoordinates) {
		this.myStreetName = name;
		for (int i = 0; i < arrayOfCoordinates.length;i++) {
			this.coordinates.add(arrayOfCoordinates[i]);
		}
	}

	@Override
	public String getId() {
		return this.myStreetName;
	}

	@Override
	public List<Coordinate> getCoordinates() {
		return this.coordinates;
	}

	@Override
	public List<Stop> getStops() {
		return this.stops;
	}

	@Override
	public boolean addStop(Stop stop) {
		Coordinate ofStop = stop.getCoordinate(); // a
		for (int i = 0; i < this.coordinates.size()-1; i++) {
			Coordinate first = this.coordinates.get(i); // b
			Coordinate second = this.coordinates.get(i+1); // c
			if ((ofStop.getX()-first.getX())*(ofStop.getY()+first.getY()) + (first.getX()-second.getX())*(first.getY()+second.getY()) + (second.getX()-ofStop.getX())*(second.getY()+ofStop.getY()) == 0) {
				if ((first.getX() <= ofStop.getX() && ofStop.getX() <= second.getX() && first.getY() <= ofStop.getY() && ofStop.getY() <= second.getY())) {
					this.stops.add(stop);

					AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(stop,i);
					stopLocation.add(e);

					stop.setStreet(this);
					return true;
				} else if ((first.getX() >= ofStop.getX() && ofStop.getX() >= second.getX() && first.getY() >= ofStop.getY() && ofStop.getY() >= second.getY())) {
					this.stops.add(stop);

					AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(stop,i);
					stopLocation.add(e);

					stop.setStreet(this);
					return true;
				} else if ((first.getX() >= ofStop.getX() && ofStop.getX() >= second.getX() && first.getY() <= ofStop.getY() && ofStop.getY() <= second.getY())) {
					this.stops.add(stop);

					AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(stop,i);
					stopLocation.add(e);

					stop.setStreet(this);
					return true;
				} else if ((first.getX() <= ofStop.getX() && ofStop.getX() <= second.getX() && first.getY() >= ofStop.getY() && ofStop.getY() >= second.getY())) {
					this.stops.add(stop);

					AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(stop,i);
					stopLocation.add(e);

					stop.setStreet(this);
					return true;
				}
			} else {
				continue;
			}
		}
		return false;
	}

	@Override
	public Object begin() {
		return this.coordinates.get(0);
	}

	@Override
	public Object end() {
		return this.coordinates.get(this.coordinates.size()-1);
	}

	@Override
	public boolean follows(Street s) {
		if (this.begin().equals(s.begin()) || this.end().equals(s.end()) || this.begin().equals(s.end()) || this.end().equals(s.begin())) {
			return true;
		}else {
			return false;
		}
	}

	@Override
	public List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStopLocation(){
		return this.stopLocation;
	}

}
