package ija.functional;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MyLine implements Line{

	private String id;
	
	private List<Street> streets = new ArrayList<>();
	private HashMap<String, String> streets_types = new HashMap<>();
	private List<Stop> stops = new ArrayList<>();
	List<SimpleImmutableEntry<Street, Stop>> line = new ArrayList<SimpleImmutableEntry<Street, Stop>> ();
	
	public MyLine(String id) {
		this.id = id;
	}

	public MyLine(Line newLine){
		this.id = newLine.getId();
		this.streets = new ArrayList<>(newLine.getStreets());
		this.stops = new ArrayList<>(newLine.getStops());
		this.line = newLine.getRoute();
		this.streets_types = newLine.getStreetsTypes();
	}

	@Override
	public HashMap<String, String> getStreetsTypes(){
		return this.streets_types;
	}
	@Override
	public void addStreetType(String street_name, String street_type){
		this.streets_types.put(street_name,street_type);
	}

	@Override
	public String getId(){
		return this.id;
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
			for (Street street_for_connet:this.streets) {
				if (street_for_connet.follows(street)) {
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
			}

			return false;
//			if (this.streets.get(this.streets.size()-1).follows(street)) {
//				this.streets.add(street);
//				if (street.getStops().isEmpty()) {
//					SimpleImmutableEntry<Street, Stop> e = new SimpleImmutableEntry<Street, Stop>(street,null);
//					line.add(e);
//				} else {
//					SimpleImmutableEntry<Street, Stop> e = new SimpleImmutableEntry<Street, Stop>(street,street.getStops().get(0));
//					line.add(e);
//				}
//				return true;
//			} else {
//				return false;
//			}
		}
		
	}

	@Override
	public boolean addStop(Stop stop) {
		if (this.addStreet(stop.getStreet())) {
			this.stops.add(stop);
			this.streets.remove(this.streets.lastIndexOf(stop.getStreet()));
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
