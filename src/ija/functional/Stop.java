package ija.functional;

import ija.sample.MainController;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javafx.scene.shape.*;
import javafx.scene.shape.Shape;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Stop implements Drawable {
    private String stop_id = "Empty";
    private Coordinate stop_cord = null;
    private Street stop_street = null;
    private Circle elements_gui;
//    private int hours = 0;
//    private int minutes = 0;
//    private int seconds = 0;
//    private int wasInStop = 0;

    public Stop(String stop_name, Coordinate... cord) {
        if (stop_name != null) {
            this.stop_id = stop_name;
        }
        try {
            this.stop_cord = cord[0];
            this.elements_gui = new Circle(cord[0].getX(), cord[0].getY(), 5, Color.ORANGE);
        } catch (Exception ignored) { }
    }

//    public Stop(Stop stop){
//        this.stop_id = stop.getId();
//        this.stop_cord = stop.getCoordinate();
//        this.stop_street = stop.getStreet();
//        this.elements_gui = (Circle)stop.getGUI().get(0);
//        this.seconds = stop.getTime();
//        this.hours = this.seconds/3600;
//        this.minutes = this.seconds/60;
//        this.wasInStop = stop.getFlag();
//    }

    public static Stop defaultStop(String id, Coordinate c){
        return new Stop(id, c);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Stop) {
            Stop stop = (Stop) o;
            if (this.hashCode() == stop.hashCode()) {
                return this.stop_id.equals(stop.getId());
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int res = 1;
        res = prime * res + stop_id.hashCode();
        return res;
    }

    public String toString() {
        return "stop(" + this.stop_id+")";

    }

    /**
     * Vrátí identifikátor zastávky.
     *
     * @return Identifikátor zastávky.
     */
    public String getId() {
        return this.stop_id;
    }

    /**
     * Vrátí pozici zastávky.
     * 
     * @return Pozice zastávky. Pokud zastávka existuje, ale dosud nemá umístění,
     *         vrací null.
     */
    public Coordinate getCoordinate() {
        return this.stop_cord;
    }

    /**
     * Nastaví ulici, na které je zastávka umístěna.
     * 
     * @param s Ulice, na které je zastávka umístěna.
     */
    public void setStreet(Street s) {
        this.stop_street = s;
    }

    /**
     * Vrátí ulici, na které je zastávka umístěna.
     * 
     * @return Ulice, na které je zastávka umístěna. Pokud zastávka existuje, ale
     *         dosud nemá umístění, vrací null.
     */
    public Street getStreet() {
        return this.stop_street;
    }

//    public Integer getTime(){
////        return Arrays.asList(this.hours,this.minutes,this.seconds);
//        return this.seconds;
//    }

//    public int getFlag(){
//        return this.wasInStop;
//    }

//    public void setFlag(Integer newFlag){
//        this.wasInStop = newFlag;
//    }

//    public void setTime(List<Integer> newTime, Bus bus){
//        this.seconds = newTime.get(0);
//        this.hours = this.seconds/3600;
//        this.minutes = this.seconds/60;
//        String dopLine = "";
//        if(this.seconds == 0){
//            dopLine = "<---------------------------bus is here!";
//            System.out.println(bus.getBusName()+"   "+this.stop_id+"   "+this.hours+":"+this.minutes+":"+(this.seconds-this.hours*3600-this.minutes*60)+dopLine);
//        }else{
//            dopLine = "";
//        }
////        System.out.println(bus.getBusName()+"   "+this.stop_id+"   "+this.hours+":"+this.minutes+":"+(this.seconds-this.hours*3600-this.minutes*60)+dopLine);
//    }

    @Override
    public List<Shape> getGUI() {
        return Collections.singletonList(this.elements_gui);
    }

    @Override
    public void setInfo(MainController controller) {
        Label label = new Label(this.getId());
        label.setVisible(false);
        label.setStyle("-fx-background-color:YELLOW");
        label.setLabelFor(this.elements_gui);
        controller.getContent().getChildren().add(label);
        this.elements_gui.setOnMouseEntered(event -> {
            label.toFront();
            label.setLayoutX(event.getSceneX() + 5);
            label.setLayoutY(event.getSceneY() - 20);
            label.setVisible(true);
            this.elements_gui.setStroke(Color.RED);
        });
        this.elements_gui.setOnMouseExited(event -> {
            label.setVisible(false);
            this.elements_gui.setStroke(Color.ORANGE);
        });
    }
}