package src.functional;

import java.util.*;

import src.Main;
import src.sample.Clock;
import src.sample.MainController;
import javafx.application.Platform;
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
    private int time_for_ring = 0; // now we have a time for bus ring
//    private int old_time_for_ring = 0;
    private int time_in_stop_left = 0; // when we spawn a bus in the stop, we have to w8 some time
    private Clock clock;
    private int seconds_to_end = -1;

    public Bus(String busName, Line busLine, String color, int time_for_ring) {
        this.checked = false;
        this.busName = busName;
        this.busLine = busLine;
        this.busColor = Color.web(color);
        this.busX = busLine.getStreets().get(0).getCoordinates().get(0).getX();
        this.busY = busLine.getStreets().get(0).getCoordinates().get(0).getY();
        this.gui = new Circle(busX, busY, 5, Color.web(color,1.0));
        this.time_for_ring = time_for_ring;
//        this.old_time_for_ring = time_for_ring;
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

//    public Integer getOldTimeForRing(){
//        return this.old_time_for_ring;
//    }

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

    public void calculatePosition(List<Integer> time){ // dont delete the comments in this method pls

        int new_time_for_ring = 0;
        for(Street street: this.busLineForUse.getStreets()){
            new_time_for_ring = new_time_for_ring + this.calculateStreetTime(street);
        }

        this.time_for_ring = new_time_for_ring;

        System.out.println("time for ring = " + this.time_for_ring);

        int position_time = (time.get(0)*3600+ time.get(1)*60+time.get(2)) % this.time_for_ring;

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

//            int street_time_in_seconds_wo_delay = (streetRange / this.speed + stopsInStreet * 3) + prev_time;
            int street_time_in_seconds_with_delay = (streetRange / (this.speed - actual_street.getDelayLevel()) + stopsInStreet * 3) + prev_time;

//            System.out.println("street_time_in_seconds_wo_delay = " + street_time_in_seconds_wo_delay);
            System.out.println("street_time_in_seconds_with_delay = " + street_time_in_seconds_with_delay);

            if(street_time_in_seconds_with_delay > position_time){ // we are in street we need, set bus position

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

                    int time_in_seconds_in_stop_with_delay = (range / (this.speed - actual_street.getDelayLevel())) + (j * 3) + prev_time;
                    int time_in_seconds_out_stop_with_delay = time_in_seconds_in_stop_with_delay + 3;
//                    int time_in_seconds_in_stop_wo_delay = (range / this.speed) + (j * 3) + prev_time;
//                    int time_in_seconds_out_stop_wo_delay = time_in_seconds_in_stop_wo_delay + 3;

                    if(time_in_seconds_in_stop_with_delay > position_time){ // before stop
                        double k = (double)(position_time - j*3 - prev_time)/(time_in_seconds_in_stop_with_delay - j*3 - prev_time);
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
                    }else if(time_in_seconds_in_stop_with_delay <= position_time && time_in_seconds_out_stop_with_delay >= position_time){ // in stop
                        this.actual_bus_street = stop.getStreet();
                        this.busX = stop.getCoordinate().getX();
                        this.busY = stop.getCoordinate().getY();
                        position_found = true;
                        this.time_in_stop_left = 3 - (position_time - time_in_seconds_in_stop_with_delay);
                        this.getBusLineForUse().addStopsTimes(stop.getId(),this.time_for_ring + this.time_in_stop_left);
//                        stop.setTime(Arrays.asList(this.time_for_ring + this.time_in_stop_left), this);
                        this.getBusLineForUse().addStopsFlags(stop.getId(), -1);
//                        stop.setFlag(-1);

                        for(Stop stop_for_readd: stops_for_readd){
                            this.busLineForUse.getStops().remove(stop_for_readd);
                            this.busLineForUse.getStops().add(stop_for_readd);
                        }

                        break;
                    }else if(position_time > time_in_seconds_out_stop_with_delay){
                        int new_time_to_stop = this.time_for_ring - (position_time-time_in_seconds_out_stop_with_delay);
//                        int old_time_to_stop = old_time_for_ring - (position_time-time_in_seconds_out_stop_with_delay);
                        this.getBusLineForUse().addStopsTimes(stop.getId(),new_time_to_stop);
//                        stop.setTime(Arrays.asList(new_time_to_stop), this);
                        this.getBusLineForUse().addStopsFlags(stop.getId(), -1);
//                        stop.setFlag(-1);
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

                double k =  (double)(position_time - stopsInStreet*3 - prev_time)/(street_time_in_seconds_with_delay - stopsInStreet*3 - prev_time);

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
                prev_time = street_time_in_seconds_with_delay;
                break;
            }else{
                prev_time = street_time_in_seconds_with_delay;
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

    public Integer calculateStreetTime(Street street){
        double rangeX = street.end().getX()-street.begin().getX();
        double rangeY = street.end().getY()-street.begin().getY();

        int range = (int)(Math.sqrt((rangeX*rangeX)+(rangeY*rangeY)));

        int stops_counter = 0;

        for(Stop stop: street.getStops()){
            if (this.getBusLineForUse().getStops().contains(stop)){
                stops_counter++;
                break;
            }
        }

        int time = (range / (this.speed - street.getDelayLevel())) + (stops_counter * 3);

        return time;
    }

    public void Move(){
        List<Street> myBusStreets = new ArrayList<>(this.busLineForUse.getStreets());
        List<Stop> myBusStops = new ArrayList<>(this.busLineForUse.getStops());

//        System.out.println("myBusStreets :" + myBusStreets);
//        System.out.println("myBusStops :" + myBusStops);

        if(this.time_in_stop_left != 0){
            try {
                Thread.sleep(this.time_in_stop_left* Main.clock.getSpeed());
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
            Integer actual_street_delay = actualStreet.getDelayLevel();

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
                    calculateAndGo(second, actual_street_delay);
                    break;
                }

                AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(myBusStops.get(0), k);// think about myBusStops.get(0) <
                if (stopLocation.contains(e)) {
                    Stop firstStop = stopLocation.get(stopLocation.indexOf(e)).getKey();
                    calculateAndGo(firstStop.getCoordinate(), actual_street_delay);
                    try {
                        Thread.sleep(Main.clock.getSpeed()*3);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    stopLocation.remove(e);
                    myBusStops.remove(0);
                } else {
                    calculateAndGo(second, actual_street_delay);
                    continue;
                }

                while (!(stopLocation.isEmpty())) {
                    AbstractMap.SimpleImmutableEntry<Stop, Integer> nextStopPair = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(myBusStops.get(0), k);
                    if (stopLocation.contains(nextStopPair)) {
                        Stop nextStop = stopLocation.get(stopLocation.indexOf(nextStopPair)).getKey();
                        calculateAndGo(nextStop.getCoordinate(), actual_street_delay);
                        try {
                            Thread.sleep(Main.clock.getSpeed()*3);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        stopLocation.remove(nextStopPair);
                        myBusStops.remove(0);
                    } else {
                        calculateAndGo(second, actual_street_delay);
                        break;
                    }
                }

                if (stopLocation.isEmpty()) {
                    calculateAndGo(second, actual_street_delay);
                }
            }
        }
    }

    public void calculateAndGo(Coordinate end, Integer delay_level){
        double rangeX = end.getX() - this.busX;
        double rangeY = end.getY() - this.busY;
        double stepX;
        double stepY;

        int j = 0;

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
            this.seconds_to_end = j; // seconds left to end this element of road
            j--;
            this.busX = this.busX + stepX;
            this.busY = this.busY + stepY;
            try {
                Thread.sleep(Main.clock.getSpeed());
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
    public void setInfo(MainController controller) {
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
                    }else{
                        for (Street street: streets){
                            street.rollBackLineColor(Bus.this.busColor);
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
