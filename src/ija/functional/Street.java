package ija.functional;

import ija.sample.BackEnd;
import ija.sample.operationsWithStreet;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Street implements Drawable {
    private final String street_name;
    private List<Coordinate> cords;
    private List<Stop> street_stops = null;
    private List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = new ArrayList<AbstractMap.SimpleImmutableEntry<Stop, Integer>> ();
    private final List<Shape> elements;
    private Boolean blocked;

    public Street(String name) {
        this.street_name = name;
        this.cords = new ArrayList<>();
        this.street_stops = new ArrayList<>();
        this.elements = new ArrayList<>();
        this.blocked = false;
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
        return this.begin().equals(s.begin()) || this.begin().equals(s.end()) || this.end().equals(s.begin())
                || this.end().equals(s.end());
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

    public static Street defaultStreet(String id, List<Coordinate> coordinates, List<Stop> stops) {
        Street street = new Street(id);
        int length = coordinates.size();
        List<Double> points = new ArrayList<>();

        if (length < 2) {
            System.out.println("There is not enough coordinates to create street");
            return null;
        } else if (length == 2) {
            street.addCoord(coordinates.get(0));
            street.addCoord(coordinates.get(1));
            points.addAll(Arrays.asList((double)coordinates.get(0).getX(),(double)coordinates.get(0).getY(), (double)coordinates.get(1).getX(),(double)coordinates.get(1).getY()));
        } else {
            for (int i = 0; i < length; i++) {
                if (i + 2 >= length) {
                    break;
                }
                Coordinate a = coordinates.get(i);
                Coordinate b = coordinates.get(i + 1);
                Coordinate c = coordinates.get(i + 2);

                int a_sqrt = (int) (Math.pow(a.diffX(b), 2) + Math.pow(a.diffY(b), 2));
                int b_sqrt = (int) (Math.pow(b.diffX(c), 2) + Math.pow(b.diffY(c), 2));
                int c_sqrt = (int) (Math.pow(c.diffX(a), 2) + Math.pow(c.diffY(a), 2));

                if (street.is_right_angle(a_sqrt, b_sqrt, c_sqrt)) {
                    // Duplicity of elements is solved inside addCoord
                    street.addCoord(a);
                    street.addCoord(b);
                    street.addCoord(c);
                    points.addAll(Arrays.asList((double)a.getX(), (double)a.getY(), (double)b.getX(), (double)b.getY(), (double)c.getX(), (double)c.getY()));
                } else {
                    return null;
                }

            }
        }

//         Setting proprietes for interacting with lines
        Polyline line = new Polyline();
        ContextMenu contextMenu = new ContextMenu();
        CheckMenuItem block = new CheckMenuItem("Bloked");
        block.setSelected(false);
        block.setOnAction(event -> {
            street.blocked = block.isSelected();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    if (street.blocked){
                        line.setStroke(Color.RED);
                    }
                    else{
                        line.setStroke(Color.BLACK);
                    }
                }
            });
            System.out.println("Street blocked? " + street.blocked);
        }
        );

//         Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(block);
        for (Double point: points){
            line.getPoints().add(point);
        }
//        pop.getContent().add(text);
        line.setStrokeWidth(3);

        line.setOnContextMenuRequested(event -> {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    contextMenu.show(line, event.getScreenX(), event.getScreenY());
                }
            });
            System.out.println("Line clicked");
        });
        final Paint[] prev_color = new Paint[1];
        line.setOnMouseEntered(event -> {
            prev_color[0] = line.getStroke();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    line.setStroke(Color.BLUE);
                }
            });
        });
        line.setOnMouseExited(event -> Platform.runLater(new Runnable() {
            @Override
            public void run() {
                line.setStroke(prev_color[0]);
            }
        }));


//        Polyline line = new Polyline();
//        for (Double point: points){
//            line.getPoints().add(point);
//        }
//
//        new Thread( ()->{
//            createLine(line);
//        }).start();
//
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        street.elements.add(line);


        if (stops != null){
            for (Stop stop : stops){
                boolean ok = street.addStop(stop);
                if (ok){
                    Circle circle = new Circle(stop.getCoordinate().getX(), stop.getCoordinate().getY(), 5, Color.ORANGE);
//                    circle.setOnMouseEntered(event -> circle.setStroke(Color.RED));
//                    circle.setOnMouseExited(event -> circle.setStroke(Color.ORANGE));
                    street.elements.add(circle);
                } else{
                    System.out.println("Stop is not in the street, exit");
                    System.exit(1);

                }

            }
        }

        return street;
    }

    public static void createLine(Polyline line){
        line.setStroke(Color.BLACK);
        line.setStrokeWidth(3);
    }

    public String getId() {
        return this.street_name;
    }

    //paint street after bus click
    public void paintStreet(String color){ //paint street after click on bus
        List<Shape> tmp = this.elements;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (Shape element : tmp) { //go through all elements of street and paint them
                        paintElement(color, element);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public void paintElement(String color, Shape element){
        element.setStroke(Color.web(color, 1.0));
    }

    private void setElements(Shape item){
        this.elements.add(item);
    }

    public List<Stop> getStops() {
        return this.street_stops;
    }

    public boolean addStop(Stop stop) {
        Coordinate coord = stop.getCoordinate();
        List<Coordinate> lst = this.getCoordinates();
        for (int i = 0; i < lst.size(); i++) {
            Coordinate first =  lst.get(i);
            Coordinate second = lst.get(i + 1);           
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
                stop.setStreet(this);
                this.street_stops.add(stop);
                    Circle circle = new Circle(stop.getCoordinate().getX(), stop.getCoordinate().getY(), 5, Color.ORANGE);
                    circle.setOnMouseEntered(event -> circle.setStroke(Color.RED));
                    circle.setOnMouseExited(event -> circle.setStroke(Color.ORANGE));
                    this.elements.add(circle);

                AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<>(stop, i);
                stopLocation.add(e);
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
        String tmp;
        if (this.street_stops != null) {
            tmp = "\tStops: " + this.street_stops.toString() + "\n}\n";
        } else {
            tmp = "\tStops: " + Arrays.toString(this.street_stops.toArray()) + "\n}\n";
        }
        return str + tmp;

    }

    public List<Coordinate> getCoordinates() {
        return this.cords;
    }

	@Override
	public List<Shape> getGUI() {
		return this.elements;
	}

    public List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStopLocation() {
            return this.stopLocation;
    }
}
