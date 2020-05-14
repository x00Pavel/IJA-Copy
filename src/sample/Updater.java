/*
    Author: Oleksii Korniienko (xkorni02)

    File: src/sample/Updater.java
    Date: 04.2020
 */

package src.sample;

import src.Main;
import src.functional.*;

import javafx.application.Platform;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Updater class for buses. Here is updating of buses is held
 */
public class Updater implements Runnable{

    private List<Bus> all_buses;

    public Updater(List<Bus> mybus){
        this.all_buses = mybus;
    }

    @Override
    public void run() {
        while(true) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    for(Bus bus: Updater.this.all_buses){
                        Circle c = (Circle)bus.getGUI().get(0);
                        c.setCenterX(bus.getBusX());
                        c.setCenterY(bus.getBusY());

                        for(int i = 0; i < bus.getBusLineForUse().getStops().size(); i++){
                            Stop stop = bus.getBusLineForUse().getStops().get(i);
                            bus.getBusLineForUse().addStopsTimes(stop.getId(), calculateTime(stop, bus, i), (bus.getTimeForRing()-bus.getOldTimeForRing()));
                        }
                    }
                }
            });
            try {
                Thread.sleep(Main.clock.getSpeed());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer calculateTime(Stop stop, Bus bus, int stopsBefore){ // calculating a time for every bus and every stop

        Street actual_street = bus.getActualBusStreet();
        List<Street> bus_streets = new ArrayList<>(bus.getBusLineForUse().getStreets());
        Street stop_street = stop.getStreet();

        if(bus.getSpeed() == 0){
            return  999;
        }

        int new_time_for_ring = 0;
        for(Street street: bus.getBusLineForUse().getStreets()){
            new_time_for_ring = new_time_for_ring + bus.calculateStreetTime(street);
        }
        bus.setTimeForRing(new_time_for_ring);

        int calibration_stops = bus.getStopsThroughs();
        int calibration_streets = bus.getStreetsThroughs();

        int time_in_seconds_with_delay = -1;

        int range = 0;

        int time_with_delay = 0;

        int flagInStop = bus.getBusLineForUse().getStopsFlags().get(stop.getId());

        if (actual_street.equals(stop_street) && flagInStop == 0) {
            double rangeX = stop.getCoordinate().getX() - bus.getBusX();
            double rangeY = stop.getCoordinate().getY() - bus.getBusY();

            range = (int) (Math.sqrt((rangeX * rangeX) + (rangeY * rangeY)));

            time_with_delay = range / (bus.getSpeed() - actual_street.getDelayLevel());
        } else {
            Coordinate actual_street_end;
            if(bus.getBusLineForUse().getStreetsTypes().get(actual_street.getId()).equals("back")){
                actual_street_end = actual_street.begin();
            }else{
                actual_street_end = actual_street.end();
            }
            double rangeX = actual_street_end.getX() - bus.getBusX();
            double rangeY = actual_street_end.getY() - bus.getBusY();

            range = (int) (Math.sqrt((rangeX * rangeX) + (rangeY * rangeY)));

            time_with_delay = range / (bus.getSpeed() - actual_street.getDelayLevel());

            int start_street_index = bus_streets.indexOf(actual_street);
            int stop_street_index = bus_streets.indexOf(stop_street);

            if(start_street_index >= stop_street_index){
                start_street_index = start_street_index - bus_streets.size();
            }

            int new_i;
            for (int i = start_street_index + 1; i < stop_street_index; i++) {
                if(i < 0){
                    new_i = i + bus_streets.size();
                }else{
                    new_i = i;
                }
                Integer street_time = calculateStreetTime(bus_streets.get(new_i), bus);
                time_with_delay = time_with_delay + street_time;
            }

            double lastRangeX;
            double lastRangeY;

            if(bus.getBusLineForUse().getStreetsTypes().get(stop_street.getId()).equals("back")){
                lastRangeX = stop.getCoordinate().getX() - stop_street.end().getX();
                lastRangeY = stop.getCoordinate().getY() - stop_street.end().getY();
            }else{
                lastRangeX = stop.getCoordinate().getX() - stop_street.begin().getX();
                lastRangeY = stop.getCoordinate().getY() - stop_street.begin().getY();
            }

            int lastRange = (int) (Math.sqrt((lastRangeX * lastRangeX) + (lastRangeY * lastRangeY)));

            int last_time_with_delay = lastRange / (bus.getSpeed() - stop_street.getDelayLevel());

            time_with_delay = time_with_delay + last_time_with_delay;
        }

        time_in_seconds_with_delay = time_with_delay + stopsBefore * 3 - calibration_stops * 3 + bus.getTimeInStopLeft();
        if(stopsBefore < calibration_stops){
            time_in_seconds_with_delay = time_in_seconds_with_delay + calibration_streets*4;
        }
        return time_in_seconds_with_delay;
    }

    public Integer calculateStreetTime(Street first, Bus bus){
        double rangeX = first.end().getX()-first.begin().getX();
        double rangeY = first.end().getY()-first.begin().getY();

        int range = (int)(Math.sqrt((rangeX*rangeX)+(rangeY*rangeY)));

        int time_with_delay = range / (bus.getSpeed() - first.getDelayLevel());
        int stops_counter = 0;
        for(Stop street_stop: first.getStops()){
            if (bus.getBusLineForUse().getStops().contains(street_stop)){
                stops_counter++;
            }
        }
        time_with_delay = time_with_delay + stops_counter*3;

        return time_with_delay;
    }
}
