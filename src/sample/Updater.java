package src.sample;

import src.Main;
import src.functional.*;
import javafx.application.Platform;
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Updater implements Runnable{

    private List<Bus> all_buses;
    // private boolean alreadyCalculated = false;
    // private List<Double> bus_start_coords = new ArrayList<>();
    // private Street bus_start_actual_street;
    // private List<Street> bus_start_bus_streets;
    // private Street bus_start_stop_street;

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
                            bus.getBusLineForUse().addStopsTimes(stop.getId(), calculateTime(stop, bus, i), (bus.getTimeForRing()-bus.getOldTimeForRing()));
                            // bus.getBusLineForUse().createOriginalStopsTimes(stop.getId(), calculateTime(stop, bus, i, true));
                            
                        }
                        // Updater.this.alreadyCalculated = true;

                        // for(Street street:bus.getBusLineForUse().getStreets()){
                        //     if(street.getPrevDelayLevel() != street.getDelayLevel()){
                        //         // for(Stop stop:bus.getBusLineForUse().getStops()){
                        //         //     bus.getBusLineForUse().addStopsFlags(stop.getId(), 0);
                        //         // }
                        //         street.setPrevDelayLevel(Integer.valueOf(street.getDelayLevel()));
                        //     }
                        // }
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

        // if(!this.alreadyCalculated && !from_start){
        //     this.bus_start_coords.add(Double.valueOf(bus.getBusX()));
        //     this.bus_start_coords.add(Double.valueOf(bus.getBusY()));
        //     // this.bus_start_actual_street = bus.getActualBusStreet();
        //     // this.bus_start_bus_streets = new ArrayList<>(bus.getBusLineForUse().getStreets());
        //     // this.bus_start_stop_street = stop.getStreet();
        // }
        
        // Double busX_for_calculate;
        // Double busY_for_calculate;
        // if(!from_start){
        //     busX_for_calculate = bus.getBusX();
        //     busY_for_calculate = bus.getBusY();
        //     // actual_street = bus.getActualBusStreet();
        //     // bus_streets = new ArrayList<>(bus.getBusLineForUse().getStreets());
        //     // stop_street = stop.getStreet();
        // }else{
        //     busX_for_calculate = this.bus_start_coords.get(0);
        //     busY_for_calculate = this.bus_start_coords.get(1);
        //     // actual_street = this.bus_start_actual_street;
        //     // bus_streets =  this.bus_start_bus_streets;
        //     // stop_street = this.bus_start_stop_street;
        // }


        int new_time_for_ring = 0;
        for(Street street: bus.getBusLineForUse().getStreets()){
            new_time_for_ring = new_time_for_ring + bus.calculateStreetTime(street);
        }
        bus.setTimeForRing(new_time_for_ring);


        // for(Stop stop_for_calculate_w8: bus.getBusLineForUse().getStops()){
        //     if(bus.getBusX() == stop_for_calculate_w8.getCoordinate().getX() && bus.getBusY() == stop_for_calculate_w8.getCoordinate().getY()){
        //         if(this.alreadyCalculated){
        //             System.out.println("alomalo");
        //             int actual_time = bus.getBusLineForUse().getStopsTimes().get(stop.getId());
        //             return actual_time-1;
        //         }else{
        //             this.alreadyCalculated = true;
        //             break;
        //         }
        //     }else{
        //         this.alreadyCalculated = false;
        //     }
        // }

        // int calibration_stops = 0;
        // if(bus.getStopsThroughs() > stopsBefore){
        //     calibration_stops = 0;
        // }else{
        int calibration_stops = bus.getStopsThroughs();
        int calibration_streets = bus.getStreetsThroughs();
        // }

        int time_in_seconds_with_delay = -1;
        // int time_in_seconds_wo_delay = -1;

        int range = 0;

        // int time_wo_delay = 0;
        int time_with_delay = 0;

        int flagInStop = bus.getBusLineForUse().getStopsFlags().get(stop.getId());

        // if(flagInStop == 0) {
            if (actual_street.equals(stop_street) && flagInStop == 0) {
                double rangeX = stop.getCoordinate().getX() - bus.getBusX();
                double rangeY = stop.getCoordinate().getY() - bus.getBusY();
    
                range = (int) (Math.sqrt((rangeX * rangeX) + (rangeY * rangeY)));
    
                // time_wo_delay = range / bus.getSpeed();
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

                // time_wo_delay = range / bus.getSpeed();
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
                    // time_wo_delay = time_wo_delay + street_time.get(1);
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
                // int last_time_wo_delay = lastRange / bus.getSpeed();

                time_with_delay = time_with_delay + last_time_with_delay;
                // time_wo_delay = time_wo_delay + last_time_wo_delay;
            }

            time_in_seconds_with_delay = time_with_delay + stopsBefore * 3 - calibration_stops * 3 + bus.getTimeInStopLeft();
            if(stopsBefore < calibration_stops){
                time_in_seconds_with_delay = time_in_seconds_with_delay + calibration_streets*4;
            }
            // time_in_seconds_wo_delay = time_wo_delay + stopsBefore * 3 - calibration_stops * 3 + bus.getTimeInStopLeft();
            // if(stopsBefore < calibration_stops){
            //     time_in_seconds_wo_delay = time_in_seconds_wo_delay + calibration_streets*4;
            // }
//            time_in_seconds_wo_delay = time_wo_delay + stopsBefore * 3 + bus.getTimeInStopLeft();

            // flagInStop = -1;
            // bus.getBusLineForUse().addStopsFlags(stop.getId(), flagInStop);
//            stop.setFlag(flagInStop);

//         }else if(flagInStop == 1){
//             time_in_seconds_with_delay = bus.getTimeForRing()+1;// mb need +1
// //            time_in_seconds_wo_delay = bus.getTimeForRing()+1;
//             flagInStop = -1;
//             bus.getBusLineForUse().addStopsFlags(stop.getId(), flagInStop);
//         }else if(flagInStop == -1){
//             time_in_seconds_with_delay = bus.getBusLineForUse().getStopsTimes().get(stop.getId()) - 1;
// //            time_in_seconds_wo_delay = bus.getBusLineForUse().getStopsTimes().get(stop.getId()) - 1;
//             if(time_in_seconds_with_delay == 0){
//                 flagInStop = 1;
//                 bus.getBusLineForUse().addStopsFlags(stop.getId(), flagInStop);
//             }
//         }
        
        // if(!from_start){
            return time_in_seconds_with_delay;
        // }else{
        //     return time_in_seconds_wo_delay-time_in_seconds_with_delay;
        // }
    }

    public Integer calculateStreetTime(Street first, Bus bus){
        double rangeX = first.end().getX()-first.begin().getX();
        double rangeY = first.end().getY()-first.begin().getY();

        int range = (int)(Math.sqrt((rangeX*rangeX)+(rangeY*rangeY)));

        int time_with_delay = range / (bus.getSpeed() - first.getDelayLevel());
        // int time_wo_delay = range / bus.getSpeed();
//        time_with_delay = range / (bus.getSpeed() - actual_street.getDelayLevel());
        int stops_counter = 0;
        for(Stop street_stop: first.getStops()){
            if (bus.getBusLineForUse().getStops().contains(street_stop)){
                stops_counter++;
            }
        }
        // System.out.println("stops_counter: " + stops_counter);
        // time_wo_delay = time_wo_delay + stops_counter*3;
        time_with_delay = time_with_delay + stops_counter*3;

        // System.out.println("time_with_delay: " + time_with_delay);

        return time_with_delay;
    }
}
