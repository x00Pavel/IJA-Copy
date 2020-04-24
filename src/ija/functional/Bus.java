package ija.functional;

import java.lang.reflect.Array;
import java.util.*;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class Bus implements Drawable {

    private String busName;
//    private final Line busLine;
    private double busX = 20;
    private double busY = 10;
    private final Circle gui;

    public Bus(String busName) {
        this.busName = busName;
//        this.busLine = busLine;
        this.gui = new Circle(busX, busY, 5, Color.RED);
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

    public void MoveDown(Circle busOne){
            busOne.setCenterY(busOne.getCenterY()+15);
    }

    public void MoveStep(Circle busOne){

    }

//    public void Move(){
//        List <Street> myBusStreets = busLine.getStreets();
//
//        List<Stop> myBusStops = busLine.getStops();
//
////        System.out.println(myBusStreets);
//
//        for (Street actualStreet : myBusStreets) {
//            List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = actualStreet.getStopLocation();
//            for (int k = 0; k < actualStreet.getCoordinates().size() - 1; k++) {
//
//                Coordinate first = actualStreet.getCoordinates().get(k);
//                Coordinate second = actualStreet.getCoordinates().get(k + 1);
//
//                AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(myBusStops.get(0), k);// think about myBusStops.get(0) <
//                if (stopLocation.contains(e)) {
//                    Stop firstStop = stopLocation.get(stopLocation.indexOf(e)).getKey();
//                    calculateAndGo(firstStop.getCoordinate(), 3);
//                    stopLocation.remove(e);
//                    myBusStops.remove(0);
//                } else {
//                    calculateAndGo(second, 3);
//                    continue;
//                }
//
//                if (stopLocation.isEmpty()) {
//                    calculateAndGo(second, 3);
//                    break;
//                }
//                while (!(stopLocation.isEmpty())) {
//                    AbstractMap.SimpleImmutableEntry<Stop, Integer> nextStopPair = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(myBusStops.get(0), k);
//                    if (stopLocation.contains(nextStopPair)) {
//                        Stop nextStop = stopLocation.get(stopLocation.indexOf(nextStopPair)).getKey();
//                        calculateAndGo(nextStop.getCoordinate(), 3);
//                        stopLocation.remove(nextStopPair);
//                        myBusStops.remove(0);
//                    } else {
//                        calculateAndGo(second, 3);
//                        break;
//                    }
//                }
//            }
//        }
//    }
//
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
//            busOne.setCenterX(busOne.getCenterX()+stepX);
//            busOne.setCenterY(busOne.getCenterY()+stepY);
            try {
                Thread.sleep(1000);
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
}
