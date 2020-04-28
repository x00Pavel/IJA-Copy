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
    private final Line busLine;
    private double busX = 0;
    private double busY = 0;
    private final Circle gui;
    private final Color busColor;
    private Boolean checked;
    private int speed = 5;
    private Street actual_bus_street = null;
    private int time_for_ring = 0;

    public Bus(String busName, Line busLine, String color, int time_for_ring) {
        this.checked = false;
        this.busName = busName;
        this.busLine = busLine;
        this.busColor = Color.web(color);
        this.busX = busLine.getStreets().get(0).getCoordinates().get(0).getX();
        this.busY = busLine.getStreets().get(0).getCoordinates().get(0).getY();
        this.gui = new Circle(busX, busY, 5, Color.web(color,1.0));
        this.time_for_ring = time_for_ring;

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

    public Integer getTimeForRing(){
        return this.time_for_ring;
    }

    public Color getColor(){
        return this.busColor;
    }

    public void Move(){
//        List <Street> myBusStreets = null;
//        myBusStreets.addAll(busLine.getStreets());
//
//        List<Stop> myBusStops = null;
//        myBusStops.addAll(busLine.getStops());

        List<Street> myBusStreets = new ArrayList<>(busLine.getStreets());
        List<Stop> myBusStops = new ArrayList<>(busLine.getStops());

//        System.out.println(myBusStreets);

        for (Street actualStreet : myBusStreets) {
            this.actual_bus_street = actualStreet;
            List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = new ArrayList<>(actualStreet.getStopLocation());
            for (int k = 0; k < actualStreet.getCoordinates().size() - 1; k++) {

                Coordinate first = actualStreet.getCoordinates().get(k);
                Coordinate second = actualStreet.getCoordinates().get(k + 1);

                if(this.getBusX() == second.getX() && this.busY == second.getY()) {
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
                Thread.sleep(Main.getClockSpeed()); // if millis 100 or less -> some bugs sometimes
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
}
