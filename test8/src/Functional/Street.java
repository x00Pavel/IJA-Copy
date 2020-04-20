package Functional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Functional.MyStreet;

public class Street implements MyStreet {
    private String street_name;
    private List<Coordinate> cords;
    private List<Stop> street_stops = null;

    public Street(String name) {
        this.street_name = name;
        this.cords = new ArrayList<>();
        this.street_stops = new ArrayList<>();
    }

    public Coordinate end() {
        List<Coordinate> lst = this.getCoordinates();
        return this.getCoordinates().get(lst.size() - 1);
    }

    public Coordinate begin() {
        List<Coordinate> lst = this.getCoordinates();
        return lst.get(0);
    }

    public boolean follows(Street s) {
        if (this.begin().equals(s.begin()) || this.begin().equals(s.end()) || this.end().equals(s.begin())
                || this.end().equals(s.end())) {
            return true;
        }
        return false;
    }

    private void addCoord(Coordinate c) {
        if (!this.cords.contains(c)) {
            this.cords.add(c);
        }
    }

    private boolean is_right_angle(int... num) {
        boolean result = false;
        if (num[0] > num[1] && num[0] > num[2]) {
            result = num[0] == num[1] + num[2];

        }
        if (num[1] > num[0] && num[1] > num[2]) {
            result = num[1] == num[0] + num[2];
        }
        if (num[2] > num[0] && num[2] > num[1]) {
            result = num[2] == num[0] + num[1];
        }
        return result;
    }

    public static Street defaultStreet(String id, Coordinate... coordinates) {
        Street street = new Street(id);
        int length = coordinates.length;
        if (length < 2) {
            System.out.println("There is not enough coordinates to create street");
            return null;
        } else if (length == 2) {
            street.addCoord(coordinates[0]);
            street.addCoord(coordinates[1]);
        } else {
            for (int i = 0; i < length; i++) {
                if (i + 2 >= length) {
                    break;
                }
                Coordinate a = coordinates[i];
                Coordinate b = coordinates[i + 1];
                Coordinate c = coordinates[i + 2];

                int a_sqrt = (int) (Math.pow(a.diffX(b), 2) + Math.pow(a.diffY(b), 2));
                int b_sqrt = (int) (Math.pow(b.diffX(c), 2) + Math.pow(b.diffY(c), 2));
                int c_sqrt = (int) (Math.pow(c.diffX(a), 2) + Math.pow(c.diffY(a), 2));

                if (street.is_right_angle(a_sqrt, b_sqrt, c_sqrt)) {
                    // Duplicity of elements is solved inside addCoord
                    street.addCoord(a);
                    street.addCoord(b);
                    street.addCoord(c);
                } else {
                    return null;
                }

            }
        }
        return street;
    }

    public String getId() {
        return this.street_name;
    }

    public List<Stop> getStops() {
        return this.street_stops;
    }

    public static Boolean onLine(Coordinate first, Coordinate second, Coordinate coord){
        int first_coord_x = (int)(Math.pow(first.diffX(coord), 2));
        int first_coord_y = (int)(Math.pow(first.diffY(coord), 2));
        int first_coord_z = first_coord_x + first_coord_y;

        int second_coord_x = (int)(Math.pow(second.diffX(coord), 2));
        int second_coord_y = (int)(Math.pow(second.diffY(coord), 2));
        int second_coord_z = second_coord_x + second_coord_y;

        int second_first_x = (int)(Math.pow(second.diffX(first), 2));
        int second_first_y = (int)(Math.pow(second.diffY(first), 2));
        int second_first_z = second_first_x + second_first_y;
        if ((int)Math.sqrt(second_coord_z) + (int)Math.sqrt(first_coord_z) == (int)Math.sqrt(second_first_z)){
            return true;
        }
        return false;
    }

    public boolean addStop(Stop stop) {
        Coordinate coord = stop.getCoordinate();
        List<Coordinate> lst = this.getCoordinates();
        for (int i = 0; i < lst.size(); i++) {
            if (Street.onLine(lst.get(i), lst.get(i + 1), coord)){
                stop.setStreet(this);
                this.street_stops.add(stop);
                return true;
            }
            if (i + 2 == lst.size()){
                break;
            }

        }
        return false;
    }

    public String toString() {
        String str = "{\n\t" + this.street_name + " - (" + this.cords.toString() + " " + this.cords.toString() + ")\n";
        if (this.street_stops != null) {
            String tmp = "\tStops: " + this.street_stops.toString() + "\n}\n";
            return str + tmp;
        } else {
            assert this.street_stops != null;
            String tmp = "\tStops: " + Arrays.toString(this.street_stops.toArray()) + "\n}\n";
            return str + tmp;
        }

    }

    public List<Coordinate> getCoordinates() {
        return this.cords;
    }

}
