package ija.functional;

import java.util.*;

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

    public Bus(String busName, Line busLine, String color) {
        this.checked = false;
        this.busName = busName;
        this.busLine = busLine;
        this.busColor = Color.web(color);
        this.busX = busLine.getStreets().get(0).getCoordinates().get(0).getX();
        this.busY = busLine.getStreets().get(0).getCoordinates().get(0).getY();
        this.gui = new Circle(busX, busY, 5, Color.web(color,1.0));

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
            List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = new ArrayList<>(actualStreet.getStopLocation());
            for (int k = 0; k < actualStreet.getCoordinates().size() - 1; k++) {

                Coordinate first = actualStreet.getCoordinates().get(k);
                Coordinate second = actualStreet.getCoordinates().get(k + 1);

                if(this.getBusX() == second.getX() && this.busY == second.getY()) {
                    second = first;
                }

                if(actualStreet.getStops().isEmpty()){
                    calculateAndGo(second, 10);
                    break;
                }

                AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(myBusStops.get(0), k);// think about myBusStops.get(0) <
                if (stopLocation.contains(e)) {
                    Stop firstStop = stopLocation.get(stopLocation.indexOf(e)).getKey();
                    calculateAndGo(firstStop.getCoordinate(), 10);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                    stopLocation.remove(e);
                    myBusStops.remove(0);
                } else {
                    calculateAndGo(second, 10);
                    continue;
                }

                while (!(stopLocation.isEmpty())) {
                    AbstractMap.SimpleImmutableEntry<Stop, Integer> nextStopPair = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(myBusStops.get(0), k);
                    if (stopLocation.contains(nextStopPair)) {
                        Stop nextStop = stopLocation.get(stopLocation.indexOf(nextStopPair)).getKey();
                        calculateAndGo(nextStop.getCoordinate(), 10);
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        stopLocation.remove(nextStopPair);
                        myBusStops.remove(0);
                    } else {
                        calculateAndGo(second, 10);
                        break;
                    }
                }

                if (stopLocation.isEmpty()) {
                    calculateAndGo(second, 10);
                }
            }
        }
    }

    public void calculateAndGo(Coordinate end, int j){
        double stepX = 0;
        double stepY = 0;

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
            this.busX = busX + stepX;
            this.busY = busY + stepY;
            try {
                Thread.sleep(250); // if millis 100 or less -> some bugs sometimes
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
