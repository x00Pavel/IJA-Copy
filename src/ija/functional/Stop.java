package ija.functional;

import javafx.scene.paint.Color;

import javafx.scene.shape.*;
import java.util.ArrayList;
import java.util.List;

public class Stop implements Drawable {
    private String stop_id = "Empty";
    private Coordinate stop_cord = null;
    private Street stop_street = null;
    private List<Shape> elements_gui;

    public Stop(String stop_name, Coordinate... cord) {
        if (stop_name != null) {
            this.stop_id = stop_name;
        }
        try {
            this.stop_cord = cord[0];
            this.elements_gui = new ArrayList<>();
            this.elements_gui.add(new Circle(cord[0].getX(), cord[0].getY(), 10, Color.ORANGE));
        } catch (Exception ignored) {
        }
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

    @Override
    public List<Shape> getGUI() {
        return this.elements_gui;
    }
}