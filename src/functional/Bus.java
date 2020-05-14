/*
    Author: Pavel Yadlouski (xyadlo00)
            Oleksii Korniienko (xkorni02)

    File: src/functional/Bus.java
    Date: 04.2020
 */

package src.functional;

import javafx.scene.control.TreeItem;
import src.Main;
import src.sample.MainController;

import java.util.*;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

/**
 * Class representing Bus object. Implements Drawable interface. Bus is representing as Circle on the map
 */
public class Bus implements Drawable {

    private TreeItem<String> root;
    private String busName;
    private MyLine busLine; // example of bus Line, try to dont use it
    private MyLine busLineForUse; // can be changed for our needs, can be used
    private double busX = 0;
    private double busY = 0;
    private final Circle gui;
    private final Color busColor;
    private Boolean checked;
    private int speed = 5;
    private Street actual_bus_street = null;
    private int time_for_ring = 0; // now we have a time for bus ring
    private int goes_through_stops = 0;
    private int goes_through_streets_with_stops;
    private int old_time_for_ring = 0;
    private int time_in_stop_left = 0; // when we spawn a bus in the stop, we have to w8 some time
    private int restart_flag = 0;
    private List<Street> streets_for_correction = new ArrayList<>();
    private boolean goBack = false;
    private boolean skipAll = false;
    private Street street_for_go_to_end = null;
    private int need_to_skip = 999;

    public Bus(String busName, MyLine busLine, String color, int time_for_ring) {
        this.checked = false;
        this.busName = busName;
        this.busLine = busLine;
        this.busColor = Color.web(color);
        this.busX = busLine.getStreets().get(0).getCoordinates().get(0).getX();
        this.busY = busLine.getStreets().get(0).getCoordinates().get(0).getY();
        this.gui = new Circle(busX, busY, 5, Color.web(color, 1.0));
        this.time_for_ring = time_for_ring;
        this.busLineForUse = new MyLine(this.busLine);
        this.root = new TreeItem<>();
        this.setBusForStops();
    }

    /**
     * Function calls method for each stop in current line to add bus to
     * list of busses in stop
     */
    private void setBusForStops() {
        TreeItem<String> stops = new TreeItem<>("Stops");
        TreeItem<String> streets = new TreeItem<>("Streets");
        for (Map.Entry<String, Integer> entry: this.busLine.getStopsTimes().entrySet()){
            for (Stop stop : this.busLine.getStops()){
                stops.getChildren().add(new TreeItem<>(stop.getId()));
                if (stop.getId().equals(entry.getKey())){
                    stop.addBus(this, entry.getValue());
                    break;
                }
            }
        }
        for (Street street: this.busLine.getStreets()){
            streets.getChildren().add(new TreeItem<>(street.getId()));
        }

        this.root.getChildren().add(stops);
        this.root.getChildren().add(streets);
        this.root.setExpanded(true);
    }

    /**
     * Method set a flag, if bus was on street was closed
     */
    public void setGoBack(){
        this.goBack = true;
    }

    /**
     * Get a X coordinate of bus
     * @return X coordinate of bus
     */
    public double getBusX() {
        return this.busX;
    }

    /**
     * Get a Y coordinate of bus
     * @return Y coordante of bus
     */
    public double getBusY() {
        return this.busY;
    }

    /**
     * Set a X coordinate of bus
     */
    public void setBusX(double tempX) {
        this.busX = tempX;
    }

    /**
     * Set an Y coordinate of bus
     */
    public void setBusY(double tempY) {
        this.busY = tempY;
    }

    /**
     * Get a name of bus
     * @return name of bus
     */
    public String getBusName() {
        return this.busName;
    }

    /**
     * Get the street on which the bus is now
     * @return street on which the bus is now
     */
    public Street getActualBusStreet() {
        return this.actual_bus_street;
    }

    /**
     * Get a speed of bus
     * @return speed of bus
     */
    public Integer getSpeed() {
        return this.speed;
    }

    /**
     * Method for stop the bus
     */
    public void pauseBus() {
        this.speed = 0;
    }

    /**
     * Method for restore movement
     */
    public void continueBus() {
        this.speed = 5;
        int new_time_for_ring = 0;
        for (Street street : this.busLineForUse.getStreets()) {
            new_time_for_ring = new_time_for_ring + this.calculateStreetTime(street);
        }
        this.old_time_for_ring = new_time_for_ring;
    }

    /**
     * When we need to restart buses movement
     */
    public void setRestartFlag() {
        this.restart_flag = 1;
    }

    /**
     * When we teleported bus on stop it will show, how long it should be here
     * @return time left to stay on stop
     */
    public Integer getTimeInStopLeft() {
        return this.time_in_stop_left;
    }

    /**
     * Get time for full bus ring
     * @return time for full bus ring
     */
    public Integer getTimeForRing() {
        return this.time_for_ring;
    }

    /**
     * Get time for full bus ring w/o street loading
     * @return time for full bus ring w/o street loading
     */
    public Integer getOldTimeForRing() {
        return this.old_time_for_ring;
    }

    /**
     * Set time for full bus ring w/o street loading
     */
    public void setOldTimeForRing(int new_time) {
        this.old_time_for_ring = new_time;
    }

    /**
     * Set time for full bus ring
     */
    public void setTimeForRing(Integer new_time_for_ring) {
        this.time_for_ring = new_time_for_ring;
    }

    /**
     * Get the number of stops the bus has traveled
     */
    public Integer getStopsThroughs() {
        return this.goes_through_stops;
    }

    /**
     * Get the number of streets the bus has traveled
     */
    public Integer getStreetsThroughs() {
        return this.goes_through_streets_with_stops;
    }

    /**
     * Get bus color
     */
    public Color getColor() {
        return this.busColor;
    }

    public Street getStreetForGoToEnd(){
        return this.street_for_go_to_end;
    }

    /**
     * Calculate start position of bus
     *
     * @param time         Time in seconds that the bus "passed"
     */
    public void calculatePosition(List<Integer> time) { // dont delete the comments in this method pls

        this.old_time_for_ring = this.time_for_ring;

        int new_time_for_ring = 0;
        for (Street street : this.busLineForUse.getStreets()) {
            new_time_for_ring = new_time_for_ring + this.calculateStreetTime(street);
        }

        this.time_for_ring = new_time_for_ring;

        // System.out.println("time for ring = " + this.time_for_ring);

        int position_time = (time.get(0) * 3600 + time.get(1) * 60 + time.get(2)) % this.time_for_ring;

        // System.out.println("position time = " + position_time);

        boolean position_found = false;

        List<Stop> stops_for_readd = new ArrayList<>();
        List<Street> streets_for_readd = new ArrayList<>();

        int prev_time = 0;

        for (int i = 0; i < this.busLine.getStreets().size(); i++) {

            Street actual_street = this.busLine.getStreets().get(i);

            int stopsInStreet = 0;

            for (Stop stop : actual_street.getStops()) {
                if (this.busLine.getStops().contains(stop)) {
                    stopsInStreet++;
                }
            }

            Coordinate start = actual_street.begin();
            Coordinate end = actual_street.end();

            if (this.busLineForUse.getStreetsTypes().get(actual_street.getId()).equals("back")) {
                start = actual_street.end();
                end = actual_street.begin();
            }

            double streetRangeX = end.getX() - start.getX();
            double streetRangeY = end.getY() - start.getY();

            int streetRange = (int) (Math.sqrt((streetRangeX * streetRangeX) + (streetRangeY * streetRangeY)));

            // int street_time_in_seconds_wo_delay = (streetRange / this.speed +
            // stopsInStreet * 3) + prev_time;
            int street_time_in_seconds_with_delay = (streetRange / (this.speed - actual_street.getDelayLevel())
                    + stopsInStreet * 3) + prev_time;

            // System.out.println("street_time_in_seconds_wo_delay = " +
            // street_time_in_seconds_wo_delay);
            // System.out.println("street_time_in_seconds_with_delay = " + street_time_in_seconds_with_delay);

            if (street_time_in_seconds_with_delay > position_time) { // we are in street we need, set bus position

                List<Stop> actual_street_stops = new ArrayList<>();

                for (Stop stop : this.busLine.getStops()) {
                    if (actual_street.getStops().contains(stop)) {
                        actual_street_stops.add(stop);
                    }
                }

                // System.out.println("actual_street_stops: " + actual_street_stops);

                for (int j = 0; j < actual_street_stops.size(); j++) {
                    Stop stop = actual_street_stops.get(j);

                    stops_for_readd.add(stop);

                    double rangeX = stop.getCoordinate().getX() - start.getX();
                    double rangeY = stop.getCoordinate().getY() - start.getY();

                    int range = (int) (Math.sqrt((rangeX * rangeX) + (rangeY * rangeY)));

                    int time_in_seconds_in_stop_with_delay = (range / (this.speed - actual_street.getDelayLevel()))
                            + (j * 3) + prev_time;
                    int time_in_seconds_out_stop_with_delay = time_in_seconds_in_stop_with_delay + 3;
                    // int time_in_seconds_in_stop_wo_delay = (range / this.speed) + (j * 3) +
                    // prev_time;
                    // int time_in_seconds_out_stop_wo_delay = time_in_seconds_in_stop_wo_delay + 3;

                    if (time_in_seconds_in_stop_with_delay > position_time) { // before stop
                        double k = (double) (position_time - j * 3 - prev_time)
                                / (time_in_seconds_in_stop_with_delay - j * 3 - prev_time);
                        double new_bus_x = rangeX * k;
                        double new_bus_y = rangeY * k;

                        this.busX = new_bus_x + start.getX();
                        this.busY = new_bus_y + start.getY();
                        position_found = true;

                        stops_for_readd.remove(stop);
                        for (Stop stop_for_readd : stops_for_readd) {
                            this.busLineForUse.getStops().remove(stop_for_readd);
                            this.busLineForUse.getStops().add(stop_for_readd);
                        }

                        break;
                    } else if (time_in_seconds_in_stop_with_delay <= position_time
                            && time_in_seconds_out_stop_with_delay >= position_time) { // in stop
                        this.actual_bus_street = stop.getStreet();
                        this.busX = stop.getCoordinate().getX();
                        this.busY = stop.getCoordinate().getY();
                        position_found = true;
                        this.time_in_stop_left = 3 - (position_time - time_in_seconds_in_stop_with_delay);
                        // this.getBusLineForUse().addStopsTimes(stop.getId(),this.time_for_ring +
                        // this.time_in_stop_left);
                        this.getBusLineForUse().addStopsFlags(stop.getId(), 1);

                        for (Stop stop_for_readd : stops_for_readd) {
                            this.busLineForUse.getStops().remove(stop_for_readd);
                            this.busLineForUse.getStops().add(stop_for_readd);
                        }

                        break;
                    } else if (position_time > time_in_seconds_out_stop_with_delay) {
                        // int new_time_to_stop = this.time_for_ring -
                        // (position_time-time_in_seconds_out_stop_with_delay);
                        // int old_time_to_stop = old_time_for_ring -
                        // (position_time-time_in_seconds_out_stop_with_delay);
                        // this.getBusLineForUse().addStopsTimes(stop.getId(),new_time_to_stop);
                        this.getBusLineForUse().addStopsFlags(stop.getId(), 1);

                        // if(j == (actual_street_stops.size()-1)){
                            streets_for_readd.add(actual_street);

                            this.street_for_go_to_end = actual_street;
                            this.need_to_skip = j;
                            this.actual_bus_street = actual_street;
                            for(Stop stop_fix: actual_street_stops){
                                if(!stops_for_readd.contains(stop_fix)){
                                    stops_for_readd.add(stop_fix);
                                }
                            }
                        // }
                    }

                }

                if (position_found) {
                    break;
                }

                // System.out.println("Street range = " + streetRange);
                //
                // System.out.println("Street time = " + street_time_in_seconds);
                //
                // System.out.println("position time = " + position_time);

                double k = (double) (position_time - stopsInStreet * 3 - prev_time)
                        / (street_time_in_seconds_with_delay - stopsInStreet * 3 - prev_time);

                // System.out.println("stopsInStreet = " + stopsInStreet);
                //
                // System.out.println("k = " + k);

                double new_bus_x = streetRangeX * k;
                double new_bus_y = streetRangeY * k;

                this.busX = new_bus_x + start.getX();
                this.busY = new_bus_y + start.getY();

                for (Stop stop_for_readd : stops_for_readd) {
                    this.busLineForUse.getStops().remove(stop_for_readd);
                    this.busLineForUse.getStops().add(stop_for_readd);
                }
                prev_time = street_time_in_seconds_with_delay;
                break;
            } else {
                prev_time = street_time_in_seconds_with_delay;
                streets_for_readd.add(actual_street);

                for (Stop stop_for_readd : this.busLine.getStops()) {
                    if (actual_street.getStops().contains(stop_for_readd)) {
                        this.busLineForUse.getStops().remove(stop_for_readd);
                        this.busLineForUse.getStops().add(stop_for_readd);
                    }
                }

                // int new_time_to_stop = this.time_for_ring -
                // (position_time-time_in_seconds_out_stop);
                // stop.setTime(Arrays.asList(new_time_to_stop), this);
                // stop.setFlag(-1);
            }
        }

        for (Street street_for_readd : streets_for_readd) {
            this.busLineForUse.getStreets().remove(street_for_readd);
            this.busLineForUse.getStreets().add(street_for_readd);
        }
    }

    /**
     * Calculate time to travel the whole street
     *
     * @param street        Street for calculate
     * @return              Time in seconds to travel the whole street
     */
    public Integer calculateStreetTime(Street street) { //mb need to delete (in Updater is a same method)
        double rangeX = street.end().getX() - street.begin().getX();
        double rangeY = street.end().getY() - street.begin().getY();

        int range = (int) (Math.sqrt((rangeX * rangeX) + (rangeY * rangeY)));
        int stops_counter = 0;
        for (Stop stop : street.getStops()) {
            if (this.getBusLineForUse().getStops().contains(stop)) {
                stops_counter++;
            }
        }

        int time = (range / (this.speed - street.getDelayLevel())) + (stops_counter * 3);

        return time;
    }

    /**
     * Method for move the bus
     *
     */
    public void Move() {

        this.busLine = new MyLine(this.busLineForUse);

        if(this.street_for_go_to_end != null){
            Coordinate end;
            Coordinate start;

            List<Stop> actual_street_stops = new ArrayList<>();
            for(Stop stop: this.street_for_go_to_end.getStops()){
                if(this.busLineForUse.getStops().contains(stop)){
                    actual_street_stops.add(stop);
                }
            }

            for(int i = this.need_to_skip+1; i < actual_street_stops.size(); i ++){
                Stop stop_to_go = actual_street_stops.get(i);
                calculateAndGo(stop_to_go.getCoordinate(), this.street_for_go_to_end, this.street_for_go_to_end.begin());
                try {
                    Thread.sleep(Main.clock.getSpeed() * 3);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }

            this.need_to_skip = 999;

            if(this.busLineForUse.getStreetsTypes().get(this.street_for_go_to_end.getId()).equals("back")){
                start = this.street_for_go_to_end.end();
                end = this.street_for_go_to_end.begin();
            }else{
                start = this.street_for_go_to_end.begin();
                end = this.street_for_go_to_end.end();
            }
            calculateAndGo(end, this.street_for_go_to_end, start);
            this.street_for_go_to_end = null;
        }

        List<Street> myBusStreets = new ArrayList<>(this.busLineForUse.getStreets());
        List<Stop> myBusStops = new ArrayList<>(this.busLineForUse.getStops());

        System.out.println("restart_flag :" + restart_flag);

        if (this.restart_flag == -1) {
            // if(actual_street_index <= blocked_street_index){
                for (Street street_for_delete : this.streets_for_correction) {
                    if (myBusStreets.contains(street_for_delete)) {
                        myBusStreets.remove(street_for_delete);
                        if (!street_for_delete.getStops().isEmpty()) {
                            for (Stop stop_for_delete : street_for_delete.getStops()) {
                                if (myBusStops.contains(stop_for_delete)) {
                                    myBusStops.remove(stop_for_delete);
                                }
                            }
                        }
                    }
                }
            // }
            this.restart_flag = 0;
        }

        System.out.println("myBusStreets :" + myBusStreets);
        System.out.println("myBusStops :" + myBusStops);

        if (this.time_in_stop_left != 0) {
            try {
                Thread.sleep(this.time_in_stop_left * Main.clock.getSpeed());
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            this.time_in_stop_left = 0;
        }

        this.streets_for_correction = new ArrayList<>();
        this.goes_through_streets_with_stops = 0;

        for (Street actualStreet : myBusStreets) {
            if (this.restart_flag == 1) {
                // while (this.speed == 0) {
                //     try {
                //         Thread.sleep(Main.clock.getSpeed()/2);
                //     } catch (InterruptedException e) {
                //         e.printStackTrace();
                //     }
                // }

                int blocked_street_index = this.busLine.getStreets().indexOf(this.busLineForUse.getBlockedStreet());
                int actual_street_index = this.busLine.getStreets().indexOf(this.actual_bus_street);
                if(actual_street_index <= blocked_street_index){
                    this.restart_flag = -1;
                    this.Move();
                    break;
                }else{
                    this.restart_flag = 0;
                }
                
            }

            this.streets_for_correction.add(actualStreet);
            this.actual_bus_street = actualStreet;
            // Integer actual_street_delay = actualStreet.getDelayLevel();

            // if(actualStreet.getId().equals(this.busLine.getStreets().get(this.busLine.getStreets().size()-1).getId())){
            // restart_flag = true;
            // }
            List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = new ArrayList<>(
                    actualStreet.getStopLocation());
            for (int k = 0; k < actualStreet.getCoordinates().size() - 1; k++) {

                Coordinate first = actualStreet.getCoordinates().get(k);
                Coordinate second = actualStreet.getCoordinates().get(k + 1);

                if (this.busLineForUse.getStreetsTypes().get(actualStreet.getId()).equals("back")) {
                    first = actualStreet.getCoordinates().get(k + 1);
                    second = actualStreet.getCoordinates().get(k);
                }

                this.skipAll = false;

                if (actualStreet.getStops().isEmpty()) {
                    calculateAndGo(second, actualStreet, first);
                    if(this.goBack){
                        this.goBack = false;
                        this.skipAll = true;
                        calculateAndGo(first, actualStreet, first);
                    }
                    break;
                }

                this.goes_through_streets_with_stops++;

                AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(
                        myBusStops.get(0), k);// think about myBusStops.get(0) <
                if (stopLocation.contains(e)) {
                    Stop firstStop = stopLocation.get(stopLocation.indexOf(e)).getKey();
                    calculateAndGo(firstStop.getCoordinate(), actualStreet, first);
                    try {
                        Thread.sleep(Main.clock.getSpeed() * 3);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    this.goes_through_stops++;
                    stopLocation.remove(e);
                    myBusStops.remove(0);
                    if(this.goBack){
                        this.goBack = false;
                        this.skipAll = true;
                        calculateAndGo(first, actualStreet, first);
                    }
                } else {
                    calculateAndGo(second, actualStreet, first);
                    if(this.goBack){
                        this.goBack = false;
                        this.skipAll = true;
                        calculateAndGo(first, actualStreet, first);
                    }
                    continue;
                }

                while (!(stopLocation.isEmpty())) {
                    AbstractMap.SimpleImmutableEntry<Stop, Integer> nextStopPair = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(
                            myBusStops.get(0), k);
                    if (stopLocation.contains(nextStopPair)) {
                        Stop nextStop = stopLocation.get(stopLocation.indexOf(nextStopPair)).getKey();
                        calculateAndGo(nextStop.getCoordinate(), actualStreet, first);
                        try {
                            Thread.sleep(Main.clock.getSpeed() * 3);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        this.goes_through_stops++;
                        stopLocation.remove(nextStopPair);
                        myBusStops.remove(0);
                        if(this.goBack){
                            this.goBack = false;
                            this.skipAll = true;
                            calculateAndGo(first, actualStreet, first);
                        }
                    } else {
                        calculateAndGo(second, actualStreet, first);
                        if(this.goBack){
                            this.goBack = false;
                            this.skipAll = true;
                            calculateAndGo(first, actualStreet, first);
                        }
                        break;
                    }
                }

                if (stopLocation.isEmpty() && !this.skipAll) {
                    calculateAndGo(second, actualStreet, first);
                    if(this.goBack){
                        this.goBack = false;
                        this.skipAll = true;
                        calculateAndGo(first, actualStreet, first);
                    }
                }
                // this.skipAll = false;

                for (Stop stop : this.busLineForUse.getStops()) {
                    if (actualStreet.getStops().contains(stop)) {
                        this.busLineForUse.addStopsFlags(stop.getId(), 0);
                    }
                }

            }
        }
        this.goes_through_stops = 0;
        // for(Stop stop:this.busLineForUse.getStops()){
        // this.busLineForUse.addStopsTimes(stop.getId(), 0);
        // this.busLineForUse.addStopsFlags(stop.getId(), 0);
        // }
    }

    /**
     * Calculate road and move to next point
     *
     * @param actual_street         Actual bus street
     * @param end                   Next point to move
     * @param start                 Previously point
     */
    public void calculateAndGo(Coordinate end, Street actual_street, Coordinate start) {
        double rangeX = end.getX() - this.busX;
        double rangeY = end.getY() - this.busY;
        double stepX;
        double stepY;

        int delay_level = actual_street.getDelayLevel();

        int j = 0;
        while(this.speed == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try{
            j = (int)((Math.sqrt((rangeX*rangeX)+(rangeY*rangeY)))/(this.speed - delay_level));
        }catch (ArithmeticException ae){
            System.out.println("[ERROR] Bus`s speed must be > 0");
            System.exit(-1);
       }

        try{
            stepX = (end.getX() - this.busX)/j;
        }catch (ArithmeticException ae){
            stepX = 0;
        }
        try{
            stepY = (end.getY() - this.busY)/j;
        }catch (ArithmeticException ae){
            stepY = 0;
        }

        while(j!=0){
            j--;
            this.busX = this.busX + stepX;
            this.busY = this.busY + stepY;
            try {
                Thread.sleep(Main.clock.getSpeed());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (this.speed == 0) {
                try {
                    Thread.sleep(Main.clock.getSpeed()/2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            int new_delay_leve = actual_street.getDelayLevel();
            if(new_delay_leve!=delay_level){
                calculateAndGo(end, actual_street, start);
                break;
            }
        }

        this.busX = end.getX();
        this.busY = end.getY();
        for(Stop stop: this.busLineForUse.getStops()){
            if(this.busX == stop.getCoordinate().getX() && this.busY == stop.getCoordinate().getY()){
                this.busLineForUse.addStopsFlags(stop.getId(), 1);
            }
        }
    }

    // Return circle that represents bus on the map
    @Override
    public List<Shape> getGUI() {
        return Collections.singletonList(this.gui);
    }

    @Override
    public void setInfo(MainController mainController) {
        this.gui.setOnMouseClicked(event -> {
            List<Street> streets = this.busLineForUse.getStreets();
            this.checked = !this.checked;
            int size = mainController.getInfoContant().getChildren().size();
            if (mainController.getInfoContant().getChildren().get(size - 1).getId().equals("busMenu")){
                if (mainController.getBusNameField().getText().equals(this.getBusName())){
                    mainController.showMainMenu(this);
                }
                else{
                    mainController.showBusMenu(this);
                }
            } else{
                mainController.showBusMenu(this);
            }
        });
    }

    public TreeItem<String> getRoot() {
        return  this.root;
    }

    public MyLine getBusLine(){
        return this.busLine;
    }
    public MyLine getBusLineForUse(){
        return this.busLineForUse;
    }
    public void setBusLineForUse(MyLine newLine){
        this.busLineForUse = newLine;
    }
}
