package ija.functional;

import java.util.*;

import ija.Main;
import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class Bus implements Drawable {

    private String busName;
    private final Line busLine; // example of bus Line, try to dont use it
    private Line busLineForUse; // can be changed for our needs, can be used
    private double busX = 0;
    private double busY = 0;
    private final Circle gui;
    private final Color busColor;
    private Boolean checked;
    private int speed = 5;
    private Street actual_bus_street = null;
    private int time_for_ring = 0; // new we have a time for bus ring
    private int time_in_stop_left = 0; // when we spawn a bus in the stop, we have to w8 some time

    public Bus(String busName, Line busLine, String color, int time_for_ring) {
        this.checked = false;
        this.busName = busName;
        this.busLine = busLine;
        this.busColor = Color.web(color);
        this.busX = busLine.getStreets().get(0).getCoordinates().get(0).getX();
        this.busY = busLine.getStreets().get(0).getCoordinates().get(0).getY();
        this.gui = new Circle(busX, busY, 5, Color.web(color,1.0));
        this.time_for_ring = time_for_ring;
        this.busLineForUse = Line.defaultLine(this.busLine);

    }



    public double getBusX(){
        return this.busX;
    }

    public double getBusY(){
        return this.busY;
    }

    public void setBusX(double tempX){
        this.busX = tempX;
    }

    public void setBusY(double tempY){
        this.busY = tempY;
    }

    public String getBusName(){
        return this.busName;
    }

    public Street getActualBusStreet(){
        return this.actual_bus_street;
    }

    public Integer getSpeed(){
        return this.speed;
    }

    public Integer getTimeInStopLeft(){ return this.time_in_stop_left; }

    public Integer getTimeForRing(){
        return this.time_for_ring;
    }

    public Color getColor(){
        return this.busColor;
    }

//    public void checkDirection(){
//        for(Street street_to_check:this.busLine.getStreets()){
//            if(street_to_check.getType().equals("back")){ // swap end and begin
//                Coordinate new_end = new Coordinate(street_to_check.begin().getX(), street_to_check.begin().getY()); // old first
//                Coordinate new_begin = new Coordinate(street_to_check.end().getX(), street_to_check.end().getY()); // old last
//                street_to_check.getCoordinates().remove(street_to_check.end());
//                street_to_check.setEnd(new_end);
//                street_to_check.getCoordinates().remove(street_to_check.begin());
//                street_to_check.setBegin(new_begin);
//            }
//        }
//        this.busLineForUse = Line.defaultLine(this.busLine); // create a copy of busline, need another pointers
//    }

    public void calculatePosition(Integer hours, Integer minutes, Integer seconds){ // dont delete the comments in this method pls
        int position_time = (hours*3600+minutes*60+seconds) % this.time_for_ring;

        System.out.println("position time = " + position_time);

        boolean position_found = false;

        List<Stop> stops_for_readd = new ArrayList<>();
        List<Street> streets_for_readd = new ArrayList<>();

        int prev_time = 0;

        for(int i = 0; i < this.busLine.getStreets().size(); i++){

            Street actual_street = this.busLine.getStreets().get(i);

            int stopsInStreet = 0;

            for (Stop stop:actual_street.getStops()) {
                if(this.busLine.getStops().contains(stop)){
                    stopsInStreet++;
                }
            }

            Coordinate start = actual_street.begin();
            Coordinate end = actual_street.end();

            if(this.busLineForUse.getStreetsTypes().get(actual_street.getId()).equals("back")) {
                start = actual_street.end();
                end = actual_street.begin();
            }

            double streetRangeX = end.getX() - start.getX();
            double streetRangeY = end.getY() - start.getY();

            int streetRange = (int) (Math.sqrt((streetRangeX * streetRangeX) + (streetRangeY * streetRangeY)));

            int street_time_in_seconds = (streetRange / this.speed + stopsInStreet * 3) + prev_time;

            System.out.println("street_time_in_seconds = " + street_time_in_seconds);

            if(street_time_in_seconds > position_time){ // we are in street we need, set bus position

                List<Stop> actual_street_stops = new ArrayList<>();

                for(Stop stop: this.busLine.getStops()){
                    if(actual_street.getStops().contains(stop)){
                        actual_street_stops.add(stop);
                    }
                }

                System.out.println("actual_street_stops: " + actual_street_stops);

                for(int j = 0; j < actual_street_stops.size(); j++){
                    Stop stop = actual_street_stops.get(j);

                    stops_for_readd.add(stop);

                    double rangeX = stop.getCoordinate().getX() - start.getX();
                    double rangeY = stop.getCoordinate().getY() - start.getY();

                    int range = (int) (Math.sqrt((rangeX * rangeX) + (rangeY * rangeY)));

                    int time_in_seconds_in_stop = (range / this.speed) + (j * 3) + prev_time;
                    int time_in_seconds_out_stop = time_in_seconds_in_stop + 3;

                    if(time_in_seconds_in_stop > position_time){ // before stop
                        double k = (double)(position_time - j*3 - prev_time)/(time_in_seconds_in_stop - j*3 - prev_time);
                        double new_bus_x = rangeX*k;
                        double new_bus_y = rangeY*k;

                        this.busX = new_bus_x + start.getX();
                        this.busY = new_bus_y + start.getY();
                        position_found = true;

                        stops_for_readd.remove(stop);
                        for(Stop stop_for_readd: stops_for_readd){
                            this.busLineForUse.getStops().remove(stop_for_readd);
                            this.busLineForUse.getStops().add(stop_for_readd);
                        }

                        break;
                    }else if(time_in_seconds_in_stop <= position_time && time_in_seconds_out_stop >= position_time){ // in stop
                        this.actual_bus_street = stop.getStreet();
                        this.busX = stop.getCoordinate().getX();
                        this.busY = stop.getCoordinate().getY();
                        position_found = true;
                        this.time_in_stop_left = 3 - (position_time - time_in_seconds_in_stop);
                        stop.setTime(Arrays.asList(this.time_for_ring + this.time_in_stop_left), this);
                        stop.setFlag(-1);

                        for(Stop stop_for_readd: stops_for_readd){
                            this.busLineForUse.getStops().remove(stop_for_readd);
                            this.busLineForUse.getStops().add(stop_for_readd);
                        }

                        break;
                    }else if(position_time > time_in_seconds_out_stop){
                        int new_time_to_stop = this.time_for_ring - (position_time-time_in_seconds_out_stop);
                        stop.setTime(Arrays.asList(new_time_to_stop), this);
                        stop.setFlag(-1);
                    }

                }

                if(position_found){
                    break;
                }

//                System.out.println("Street range = " + streetRange);
//
//                System.out.println("Street time = " + street_time_in_seconds);
//
//                System.out.println("position time = " + position_time);

                double k =  (double)(position_time - stopsInStreet*3 - prev_time)/(street_time_in_seconds - stopsInStreet*3 - prev_time);

//                System.out.println("stopsInStreet = " + stopsInStreet);
//
//                System.out.println("k = " + k);

                double new_bus_x = streetRangeX*k;
                double new_bus_y = streetRangeY*k;

                this.busX = new_bus_x + start.getX();
                this.busY = new_bus_y + start.getY();

                for(Stop stop_for_readd: stops_for_readd){
                    this.busLineForUse.getStops().remove(stop_for_readd);
                    this.busLineForUse.getStops().add(stop_for_readd);
                }
                prev_time = street_time_in_seconds;
                break;
            }else{
                prev_time = street_time_in_seconds;
                streets_for_readd.add(actual_street);

                for(Stop stop_for_readd: this.busLine.getStops()){
                    if(actual_street.getStops().contains(stop_for_readd)){
                        this.busLineForUse.getStops().remove(stop_for_readd);
                        this.busLineForUse.getStops().add(stop_for_readd);
                    }
                }

//                int new_time_to_stop = this.time_for_ring - (position_time-time_in_seconds_out_stop);
//                stop.setTime(Arrays.asList(new_time_to_stop), this);
//                stop.setFlag(-1);
            }
        }

        for(Street street_for_readd: streets_for_readd){
            this.busLineForUse.getStreets().remove(street_for_readd);
            this.busLineForUse.getStreets().add(street_for_readd);
        }

    }

    public void Move(){
        List<Street> myBusStreets = new ArrayList<>(this.busLineForUse.getStreets());
        List<Stop> myBusStops = new ArrayList<>(this.busLineForUse.getStops());

//        System.out.println("myBusStreets :" + myBusStreets);
//        System.out.println("myBusStops :" + myBusStops);

        if(this.time_in_stop_left != 0){
            try {
                Thread.sleep(this.time_in_stop_left*Main.getClockSpeed());
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            this.time_in_stop_left = 0;
        }

        boolean restart_flag = false;

        for (Street actualStreet : myBusStreets) {
            if(restart_flag){
                break;
            }
            this.actual_bus_street = actualStreet;
            if(actualStreet.getId().equals(this.busLine.getStreets().get(this.busLine.getStreets().size()-1).getId())){
                restart_flag = true;
            }
            List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = new ArrayList<>(actualStreet.getStopLocation());
            for (int k = 0; k < actualStreet.getCoordinates().size() - 1; k++) {

                Coordinate first = actualStreet.getCoordinates().get(k);
                Coordinate second = actualStreet.getCoordinates().get(k + 1);

                if(this.busLineForUse.getStreetsTypes().get(actualStreet.getId()).equals("back")){
                    second = first;
                }

                if(actualStreet.getStops().isEmpty()){
                    calculateAndGo(second);
                    break;
                }

                AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(myBusStops.get(0), k);// think about myBusStops.get(0) <
                if (stopLocation.contains(e)) {
                    Stop firstStop = stopLocation.get(stopLocation.indexOf(e)).getKey();
                    calculateAndGo(firstStop.getCoordinate());
                    try {
                        Thread.sleep(Main.getClockSpeed()*3);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    stopLocation.remove(e);
                    myBusStops.remove(0);
                } else {
                    calculateAndGo(second);
                    continue;
                }

                while (!(stopLocation.isEmpty())) {
                    AbstractMap.SimpleImmutableEntry<Stop, Integer> nextStopPair = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(myBusStops.get(0), k);
                    if (stopLocation.contains(nextStopPair)) {
                        Stop nextStop = stopLocation.get(stopLocation.indexOf(nextStopPair)).getKey();
                        calculateAndGo(nextStop.getCoordinate());
                        try {
                            Thread.sleep(Main.getClockSpeed()*3);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        stopLocation.remove(nextStopPair);
                        myBusStops.remove(0);
                    } else {
                        calculateAndGo(second);
                        break;
                    }
                }

                if (stopLocation.isEmpty()) {
                    calculateAndGo(second);
                }
            }
        }
    }

    public void calculateAndGo(Coordinate end){
        double rangeX = end.getX() - this.busX;
        double rangeY = end.getY() - this.busY;
        double stepX;
        double stepY;

        int j = 0;

        try{
            j = (int)((Math.sqrt((rangeX*rangeX)+(rangeY*rangeY)))/this.speed);
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
                Thread.sleep(Main.getClockSpeed());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.busX = end.getX();
        this.busY = end.getY();
    }

    // Return circle that represents bus on the map
    @Override
    public List<Shape> getGUI() {
        return Collections.singletonList(this.gui);
    }

    @Override
    public void setInfo(Pane container) {
        this.gui.setOnMouseClicked(event -> {
            List<Street> streets = this.busLine.getStreets();
            this.checked = !this.checked;
            boolean c = this.checked;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (c){
                        for (Street street: streets){
                            street.changeLineColor(Bus.this.busColor);
                        }
                    }
                    else{
                        for (Street street: streets){
                            street.rollBackLineColor();
                        }
                    }
                }
            });
        });
    }

    public Line getBusLine(){
        return this.busLine;
    }
    public Line getBusLineForUse(){
        return this.busLineForUse;
    }
    public void setBusLineForUse(Line newLine){
        this.busLineForUse = newLine;
    }
}
