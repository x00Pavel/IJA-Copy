package ija.sample;

import ija.Main;
import ija.functional.Bus;
import ija.functional.Coordinate;
import ija.functional.Stop;
import ija.functional.Street;
import javafx.application.Platform;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

//                        Stop stop = bus.getBusLine().getStops().get(0); // 147 seconds full road
//                        for(Stop stop: bus.getBusLine().getStops()){
//                            stop.setTime(calculateTime(stop, bus));
//                        }

                        for(int i = 0; i < bus.getBusLine().getStops().size(); i++){
                            Stop stop = bus.getBusLine().getStops().get(i);
                            stop.setTime(calculateTime(stop, bus, i), bus);
                        }
                    }
                }
            });
            try {
                Thread.sleep(Main.getClockSpeed());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Integer> calculateTime(Stop stop, Bus bus, int stopsBefore){
        Street actual_street = bus.getActualBusStreet();
        List<Street> bus_streets = new ArrayList<>(bus.getBusLine().getStreets());
        Street stop_street = stop.getStreet();

        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        int time_in_seconds = -1;

        int range = 0;

        int flagInStop = stop.getFlag();

        if(flagInStop == 0) {
            if (actual_street.equals(stop_street)) {
                double rangeX = stop.getCoordinate().getX() - bus.getBusX();
                double rangeY = stop.getCoordinate().getY() - bus.getBusY();

                range = (int) (Math.sqrt((rangeX * rangeX) + (rangeY * rangeY)));
            } else {
                Coordinate actual_street_end = actual_street.end();
                double rangeX = actual_street_end.getX() - bus.getBusX();
                double rangeY = actual_street_end.getY() - bus.getBusY();

                range = (int) (Math.sqrt((rangeX * rangeX) + (rangeY * rangeY)));

                bus_streets.indexOf(actual_street);
                bus_streets.indexOf(stop_street);

                for (int i = bus_streets.indexOf(actual_street) + 1; i < bus_streets.indexOf(stop_street); i++) {
                    int street_range = calculateStreetRange(bus_streets.get(i));
                    range = range + street_range;
                }

                double lastRangeX = stop.getCoordinate().getX() - stop_street.begin().getX();
                double lastRangeY = stop.getCoordinate().getY() - stop_street.begin().getY();

                int lastRange = (int) (Math.sqrt((lastRangeX * lastRangeX) + (lastRangeY * lastRangeY)));

                range = range + lastRange;
            }

            time_in_seconds = range / bus.getSpeed() + stopsBefore * 3;

            flagInStop = -1;
            stop.setFlag(flagInStop);

        }else if(flagInStop == 1){
            time_in_seconds = bus.getTimeForRing();
            flagInStop = -1;
            stop.setFlag(flagInStop);
        }else if(flagInStop == -1){
            time_in_seconds = stop.getTime() - 1;
            if(time_in_seconds == 0){
                flagInStop = 1;
                stop.setFlag(flagInStop);
            }
        }

        return Arrays.asList(time_in_seconds);
    }

    public Integer calculateStreetRange(Street first){
        double rangeX = first.end().getX()-first.begin().getX();
        double rangeY = first.end().getY()-first.begin().getY();

        int range = (int)(Math.sqrt((rangeX*rangeX)+(rangeY*rangeY)));

        return range;
    }
}
