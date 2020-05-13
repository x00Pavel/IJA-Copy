/*
    Author: Pavel Yadlouski (xyadlo00)
            Oleksii Korniienko (xkorni02)

    File: src/sample/MainController.java
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
    }

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
            int size = controller.getInfoContant().getChildren().size();
            if (controller.getInfoContant().getChildren().get(size - 1).getId().equals("stopMenu")){
                if (controller.getStopNameField().getText().equals(this.getId())){
                    controller.hideStopMenu();
                }else {
                    controller.showStopMenu(this.getId(), this.listHBox);
                }
            }
            else {
                controller.showStopMenu(this.getId(), this.listHBox);
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

    /**
     * Method for setting information in side menu
     //     * @param infoContant
     */
    public void addBus(Bus bus, Integer time) {
        this.listBuses.add(bus);
        HBox box = new HBox(2);
        Text busName = new Text(bus.getBusName() + "->");
        VBox vBox = new VBox(1);
        vBox.getChildren().add(new Text(String.valueOf(time)));
        box.getChildren().addAll(busName, vBox);
        this.listHBox.add(box);
    }
}