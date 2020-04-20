package Functional;

import Functional.MyStop;

public class Stop implements MyStop{
    private String stop_id = "Empty";
    private Coordinate stop_cord = null;
    private Street stop_street = null;

    public Stop(String stop_name, Coordinate... cord) {
        if (stop_name != null ) {
            this.stop_id = stop_name;
        }

        try {
            this.stop_cord = cord[0];
        } catch (Exception e) {
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
        String str = "stop(" + this.stop_id+")";
        return str;
        // if (this.stop_id != null & this.stop_cord != null) {
        //     String str = "{\n\tStop ID:" + this.stop_id + "\n\tCoordinates: " + this.stop_cord.toString() + "\n";
        //     // if (this.stop_street != null) {
        //     //     String tmp = "\tStreet: there is some string }\n";
        //     //     return str + tmp;
        //     // } else {
        //     //     String tmp = "}\n";
        //     //     return str + tmp;
        //     // }

        // } else {
        //     if (this.stop_id != null) {
        //         System.out.println("stop id is null");
        //     }
        //     if (this.stop_street != null) {
        //         System.out.println("Stop street is null");
        //     }

        //     if (this.stop_cord != null) {
        //         System.out.println("Cord is null");
        //     }
        // }
        // return "Not Nice";
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

}