/*
    Author: Pavel Yadlouski (xyadlo00)
            Oleksii Korniienko (xkorni02)

    File: src/functional/MyLine.java
    Date: 04.2020
 */


package src.functional;

import src.sample.MainController;

import javafx.scene.shape.Shape;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Implementation of bus line.
 */
public class MyLine implements Line{

	private String id;
	
	private List<Street> streets = new ArrayList<>();
	private List<Street> temp_new_streets = new ArrayList<>();
	private List<Street> streets_was_painted = new ArrayList<>();
	private HashMap<String, String> streets_types = new HashMap<>();
	private List<Stop> stops = new ArrayList<>();
	private List<Stop> temp_new_stops = new ArrayList<>();
	private HashMap<String, Integer> stops_times = new HashMap<>(); // name_of_stop:time_to_stop_left
	private HashMap<String, Integer> stops_flags = new HashMap<>();
	private int line_delay = 0;
	List<SimpleImmutableEntry<Street, Stop>> line = new ArrayList<SimpleImmutableEntry<Street, Stop>> ();
	private Street blocked_street;
	
	public MyLine(String id) {
		this.id = id;
	}

	public MyLine(Line newLine){
		this.id = newLine.getId();
		this.streets = new ArrayList<>(newLine.getStreets());
		this.stops = new ArrayList<>(newLine.getStops());
		this.line = newLine.getRoute();
		this.streets_types = newLine.getStreetsTypes();
		this.stops_flags = newLine.getStopsFlags();
		this.stops_times = newLine.getStopsTimes();
	}

	@Override
	public void setNewStreets(List<Street> new_streets){
		this.streets = new_streets;
	}

	@Override
	public void setNewStops(List<Stop> new_stops){
		this.stops = new_stops;
	}

	@Override
	public List<Stop> getTempNewStops(){
		return this.temp_new_stops;
	}
	@Override
	public void addTempNewStop(Stop new_stop){
		this.temp_new_stops.add(new_stop);
	}

	@Override
	public List<Street> getTempNewStreet(){
		return this.temp_new_streets;
	}

	@Override
	public List<Street> getPaintedStreet(){
		return this.streets_was_painted;
	}

	@Override
	public void setBlockedStreet(Street new_blocked_street){
		this.blocked_street = new_blocked_street;
	}

	@Override
	public Street getBlockedStreet(){
		return this.blocked_street;
	}

	@Override
	public HashMap<String, String> getStreetsTypes(){
		return this.streets_types;
	}

	@Override
	public HashMap<String, Integer> getStopsTimes() {
		return this.stops_times;
	}

	@Override
	public HashMap<String, Integer> getStopsFlags() {
		return this.stops_flags;
	}


	@Override
	public void addStreetType(String street_name, String street_type){
		this.streets_types.put(street_name, street_type);
	}

	@Override
	public void addStopsTimes(String stop_name, Integer stop_times, Integer delay){
		this.stops_times.put(stop_name, stop_times);
		this.line_delay = delay;
		int seconds = stop_times;
        int hours = seconds/3600;
        int minutes = seconds/60;
        String dopLine = "";
        if(seconds == 0){
            dopLine = "<---------------------------bus is here!";
            System.out.println(this.id+"   "+stop_name+"   "+hours+":"+minutes+":"+(seconds-hours*3600-minutes*60)+dopLine + "     delay: " + this.line_delay);
        }else{
            dopLine = "";
		}
	}

	@Override
	public void addStopsFlags(String stop_name, Integer stop_flag){
		this.stops_flags.put(stop_name, stop_flag);
	}

	@Override
	public String getId(){
		return this.id;
	}

	@Override
	public boolean addStreet(Street street) {
		if (this.streets.isEmpty()) {
			if (street.getStops().isEmpty()) {
				return false;
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
	public void setInfo(MainController controller) {

	}
}
