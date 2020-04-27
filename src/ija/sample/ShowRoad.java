package ija.sample;

import ija.functional.Bus;
import ija.functional.Street;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShowRoad implements Runnable{

    private Bus bus;
    private Circle bus_circle;
    private Color color;
    private boolean checkClicked;

    public ShowRoad(Bus mybus, Color color){
        this.bus = mybus;
        this.bus_circle = (Circle)mybus.getGUI().get(0);
        this.color = color;
    }

    @Override
    public void run() {

    }

//    @Override
//    public void run() {
//        this.checkClicked = false;
//
//        this.bus_circle.setOnMouseClicked(event -> {
////            prev_color[0] = this.bus_circle.getStroke();
////            this.bus_circle.setStroke(Color.BLACK);
//            List<Street> busStreets = this.bus.getBusLine().getStreets();
////            String prev_color = null;
//            if (this.checkClicked){
//                for (Street street:busStreets) {
//                    Color color = this.color;
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            street.paintStreet(color, "back");
//                        }
//                    });
//                }
//                this.checkClicked = false;
//            }else{
//                for (Street street:busStreets) {
//                    Color color = this.color;
//                    Platform.runLater(new Runnable() {
//                        @Override
//                        public void run() {
//                            street.paintStreet(color, "go");
//                        }
//                    });
//                }
//                this.checkClicked = true;
//            }
//        });
//    }
}
