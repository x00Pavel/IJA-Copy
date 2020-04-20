package Functional;

import java.net.URL;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import sample.BackEnd;

public class Bus extends Circle{

    private String busName;
    private final Line busLine;

    private Paint randomColor() {
        Random random = new Random();
        int r = random.nextInt(255);
        int g = random.nextInt(255);
        int b = random.nextInt(255);
        return Color.rgb(r, g, b);
    }

    public Bus(String busName, int i) {
        this.busName = busName;
        this.setRadius(5);
        this.setFill(randomColor());
        this.busLine  = BackEnd.lines.get(i);
        Street f_street = BackEnd.streets.get(i);
        this.setBusX(f_street.getCoordinates().get(0).getX());
        this.setBusY(f_street.getCoordinates().get(0).getY());
    }
    public Functional.Line getLine(){
        return this.busLine;
    }
    public double getBusX(){
        return this.getCenterX();
    }

    public double getBusY(){
        return this.getCenterY();
    }

    public void setBusX(double tempX){
        this.setCenterX(tempX);
    }

    public void setBusY(double tempY){
        this.setCenterY(tempY);
    }

//    public static void  GetLine(){
//        return this.busLine;
//    }

    public void MoveDown(Circle busOne){
            busOne.setCenterY(busOne.getCenterY()+15);
    }

    public void MoveStep(Circle busOne){

    }

//    public void Move(){
//        List <Street> myBusStreets = busLine.getStreets();
//
//        List <Stop> myBusStops = busLine.getStops();
//
////        System.out.println(myBusStreets);
//
//        for (int f = 0; f < myBusStreets.size(); f++){
//            Street actualStreet = myBusStreets.get(f);
//            List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = actualStreet.getStopLocation();
//            for (int k = 0; k < myBusStreets.get(f).getCoordinates().size()-1; k++){
//
//                Coordinate first = actualStreet.getCoordinates().get(k);
//                Coordinate second = actualStreet.getCoordinates().get(k+1);
//
//                AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(myBusStops.get(0),k);// think about myBusStops.get(0) <
//                if (stopLocation.contains(e)) {
//                    Stop firstStop = stopLocation.get(stopLocation.indexOf(e)).getKey();
//                    calculateAndGo(firstStop.getCoordinate(), 3);
//                    stopLocation.remove(e);
//                    myBusStops.remove(0);
//                }else{
//                    calculateAndGo(second, 3);
//                    continue;
//                }
//
//                if (stopLocation.isEmpty()){
//                    calculateAndGo(second, 3);
//                    break;
//                }
//                while (!(stopLocation.isEmpty())){
//                    AbstractMap.SimpleImmutableEntry<Stop, Integer> nextStopPair = new AbstractMap.SimpleImmutableEntry<Stop, Integer>(myBusStops.get(0),k);
//                    if (stopLocation.contains(nextStopPair)) {
//                        Stop nextStop = stopLocation.get(stopLocation.indexOf(nextStopPair)).getKey();
//                        calculateAndGo(nextStop.getCoordinate(), 3);
//                        stopLocation.remove(nextStopPair);
//                        myBusStops.remove(0);
//                    }else{
//                        calculateAndGo(second, 3);
//                        break;
//                    }
//                }
//            }
//        }
//    }

//    public void calculateAndGo(Coordinate end, int j){
//        double stepX = 0;
//        double stepY = 0;
//
//        try{
//            stepX = (end.getX() - this.busX)/j;
//        }catch (ArithmeticException ae){
//            stepX = 0;
//        }
//        try{
//            stepY = (end.getY() - this.busY)/j;
//        }catch (ArithmeticException ae){
//            stepY = 0;
//        }
//
//        while(j!=0){
//            j--;
//            this.busX = busX + stepX;
//            this.busY = busY + stepY;
////            busOne.setCenterX(busOne.getCenterX()+stepX);
////            busOne.setCenterY(busOne.getCenterY()+stepY);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
