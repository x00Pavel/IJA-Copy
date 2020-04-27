package ija.functional;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyLine implements Line{
	
	@SuppressWarnings("unused")
	private String id;
	
	private List<Street> streets = new ArrayList<>();
	private List<Stop> stops = new ArrayList<>();
	List<SimpleImmutableEntry<Street, Stop>> line = new ArrayList<SimpleImmutableEntry<Street, Stop>> ();
	
	public MyLine(String id) {
		this.id = id;
	}

	@Override
	public boolean addStreet(Street street) {
		if (this.streets.isEmpty()) {
			if (street.getStops().isEmpty()) {
				return false; // ----------------------------------------------------Think about it
			} else {
				this.streets.add(street);
				if (street.getStops().isEmpty()) {
					SimpleImmutableEntry<Street, Stop> e = new SimpleImmutableEntry<Street, Stop>(street,null);
					line.add(e);
				} else {
					SimpleImmutableEntry<Street, Stop> e = new SimpleImmutableEntry<Street, Stop>(street,street.getStops().get(0));
					line.add(e);
				}
				return true;
			}
		} else {
			if (this.streets.get(this.streets.size()-1).follows(street)) {
				this.streets.add(street);
				if (street.getStops().isEmpty()) {
					SimpleImmutableEntry<Street, Stop> e = new SimpleImmutableEntry<Street, Stop>(street,null);
					line.add(e);
				} else {
					SimpleImmutableEntry<Street, Stop> e = new SimpleImmutableEntry<Street, Stop>(street,street.getStops().get(0));
					line.add(e);
				}
				return true;
			} else {
				return false;
			}
		}
		
	}

	@Override
	public boolean addStop(Stop stop) {
		if (this.addStreet(stop.getStreet())) {
			this.stops.add(stop);
			this.streets.remove(stop.getStreet());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<SimpleImmutableEntry<Street, Stop>> getRoute() {
		return Collections.unmodifiableList(this.line);
	}

	@Override
	public List<Stop> getStops(){
		return this.stops;
	}

	@Override
	public List<Street> getStreets(){
		return this.streets;
	}

	@Override
	public List<Shape> getGUI() {
		return null;
	}

	@Override
	public void setInfo(Pane container) {

	}
}
