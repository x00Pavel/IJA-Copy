package ija.sample;

import ija.Main;
import ija.functional.Bus;
import ija.functional.Coordinate;
import ija.functional.Stop;
import ija.functional.Street;
import javafx.application.Platform;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
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

                        for(int i = 0; i < bus.getBusLineForUse().getStops().size(); i++){
                            Stop stop = bus.getBusLineForUse().getStops().get(i);
//                            stop.setTime(calculateTime(stop, bus, i), bus);
                            bus.getBusLineForUse().addStopsTimes(stop.getId(), calculateTime(stop, bus, i));
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

        int time_in_seconds_with_delay = -1;
//        int time_in_seconds_wo_delay = -1;

        int range = 0;

        int time_wo_delay = 0;
        int time_with_delay = 0;

        int flagInStop = bus.getBusLineForUse().getStopsFlags().get(stop.getId());

        if(flagInStop == 0) {
            if (actual_street.equals(stop_street)) {
                double rangeX = stop.getCoordinate().getX() - bus.getBusX();
                double rangeY = stop.getCoordinate().getY() - bus.getBusY();

                range = (int) (Math.sqrt((rangeX * rangeX) + (rangeY * rangeY)));

//                time_wo_delay = range / bus.getSpeed();
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

//                time_wo_delay = range / bus.getSpeed();
                time_with_delay = range / (bus.getSpeed() - actual_street.getDelayLevel());

                for (int i = bus_streets.indexOf(actual_street) + 1; i < bus_streets.indexOf(stop_street); i++) {
                    int street_time = calculateStreetTime(bus_streets.get(i), bus.getSpeed());
//                    time_wo_delay = time_wo_delay + street_time.get(0);
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
//                int last_time_wo_delay = lastRange / bus.getSpeed();

                time_with_delay = time_with_delay + last_time_with_delay;
//                time_wo_delay = time_wo_delay + last_time_wo_delay;
            }

            time_in_seconds_with_delay = time_with_delay + stopsBefore * 3 + bus.getTimeInStopLeft();
//            time_in_seconds_wo_delay = time_wo_delay + stopsBefore * 3 + bus.getTimeInStopLeft();

            flagInStop = -1;
            bus.getBusLineForUse().addStopsFlags(stop.getId(), flagInStop);
//            stop.setFlag(flagInStop);

        }else if(flagInStop == 1){
            time_in_seconds_with_delay = bus.getTimeForRing()+1; // mb need +1
//            time_in_seconds_wo_delay = bus.getTimeForRing()+1;
            flagInStop = -1;
            bus.getBusLineForUse().addStopsFlags(stop.getId(), flagInStop);
        }else if(flagInStop == -1){
            time_in_seconds_with_delay = bus.getBusLineForUse().getStopsTimes().get(stop.getId()) - 1;
//            time_in_seconds_wo_delay = bus.getBusLineForUse().getStopsTimes().get(stop.getId()) - 1;
            if(time_in_seconds_with_delay == 0){
                flagInStop = 1;
                bus.getBusLineForUse().addStopsFlags(stop.getId(), flagInStop);
            }
        }

        return time_in_seconds_with_delay;
    }

    public Integer calculateStreetTime(Street first, Integer speed){
        double rangeX = first.end().getX()-first.begin().getX();
        double rangeY = first.end().getY()-first.begin().getY();

        int range = (int)(Math.sqrt((rangeX*rangeX)+(rangeY*rangeY)));

        int time_with_delay = range / (speed - first.getDelayLevel());
//        int time_wo_delay = range / speed;
//        time_with_delay = range / (bus.getSpeed() - actual_street.getDelayLevel());

        return time_with_delay;
    }
}
