/*
    Author: Pavel Yadlouski (xyadlo00)
            Oleksii Korniienko (xkorni02)

    File: src/functional/Stop.java
    Date: 04.2020
 */


package src.functional;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import src.sample.MainController;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import javafx.scene.shape.*;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import src.Main;

/**
 * Implementation of bus Stop object.
 */
public class Stop implements Drawable {
    private String stop_id = "Empty";
    private Coordinate stop_cord = null;
    private Street stop_street = null;
    private Circle elements_gui;
    private List<Bus> listBuses;
    private List<HBox> listHBox;
    private List<MyLine> listLines;

    /**
     * Constructor for Stop object
     * 
     * @param stop_name Name of new stop
     * @param cord Coordinates of stop
     */
    public Stop(String stop_name, Coordinate... cord) {
        if (stop_name != null) {
            this.stop_id = stop_name;
        }
        try {
            this.stop_cord = cord[0];
            this.elements_gui = new Circle(cord[0].getX(), cord[0].getY(), 5, Color.ORANGE);
        } catch (Exception ignored) { }
        this.listBuses = new ArrayList<>();
        this.listHBox = new ArrayList<>();
        this.listLines = new ArrayList<>();
    }

    /**
     * Create a new instance of Stop
	 * 
	 * @return 				New instance of Stop
     */
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

    /**
     * Represent a stop as string
     *
     */
    public String toString() {
        return "stop(" + this.stop_id+")";

    }

    /**
     * Get a stop name (id)
     *
     * @return              Stop name (id)
     */
    public String getId() {
        return this.stop_id;
    }

    /**
     * Get a coordinate of stop
     * 
     * @return              Coordinate of stop
     */
    public Coordinate getCoordinate() {
        return this.stop_cord;
    }

    /**
     * Set a stop`s street
     * 
     * @param s             Street on which the stop is located
     */
    public void setStreet(Street s) {
        this.stop_street = s;
    }

    /**
     * Get a stop`s street
     * 
     * @return              Street on which the stop is located
     */
    public Street getStreet() {
        return this.stop_street;
    }

    /**
     * Method for getting graphical representation of Stop object
     * @return list of shapes that corresponds to the Stop
     */
    @Override
    public List<Shape> getGUI() {
        return Collections.singletonList(this.elements_gui);
    }

    /**
     * Method prepare information that would be inserted on side menu request
     * @param controller Main controller of scene
     */
    @Override
    public void setInfo(MainController controller) {
        Label label = new Label(this.getId());
        label.setVisible(false);
        label.setStyle("-fx-background-color:YELLOW");
        label.setLabelFor(this.elements_gui);
        controller.getMapParent().getChildren().add(label);

        this.elements_gui.setOnMouseClicked(event -> {
            if (Main.controller.getMode().equals("default")) {
                int size = controller.getInfoContant().getChildren().size();
                if (controller.getInfoContant().getChildren().get(size - 1).getId().equals("stopMenu")){
                    if (controller.getStopNameField().getText().equals(this.getId())){
                        controller.showMainMenu();
                    }else {
                        controller.showStopMenu(this.getId(), this.listBuses);
                    }
                }
                else {
                    controller.showStopMenu(this.getId(), this.listBuses);
                }
            }else{
                System.out.println("Mode is EDIT!");
                System.out.println("stop_name: " + this.getId());
                List<Bus> buses_need = new ArrayList<>();
                for(Bus bus: Main.controller.getListBuses()){
                    if (bus.getSpeed() == 0) {
                        buses_need.add(bus);
                    }
                }
                System.out.println("buses_need: " + buses_need);
                if(!buses_need.isEmpty()){
                    Bus bus = buses_need.get(0);
                    MyLine bus_line = bus.getBusLine();
                    Street last_clicked_street = bus_line.getTempNewStreet().get(bus_line.getTempNewStreet().size()-1);
                    if(last_clicked_street.getStops().contains(this)){
                        bus_line.addTempNewStop(this);

                        System.out.println("Stop was added");
                        System.out.println("bus_name: " + bus.getBusName());
                        System.out.println("new_stops: " + bus_line.getTempNewStops());
                    }else{
                        System.out.println("You can`t choose stop out of actual street!");
                    }
                }
            }
        });

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

    public void addBus(Bus bus, Integer time) {
        if (!this.listBuses.contains(bus)){
            if(!this.listLines.contains(bus.getBusLine())){
                this.listLines.add(bus.getBusLine());
                this.listBuses.add(bus);
                HBox box = new HBox(2);
                Text busName = new Text(bus.getBusName() + "->");
                VBox vBox = new VBox(1);
                vBox.getChildren().add(new Text(String.valueOf(time)));
                box.getChildren().addAll(busName, vBox);
                this.listHBox.add(box);

            }
        }
    }
}