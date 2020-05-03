package ija.functional;

import ija.Main;
import ija.sample.MainController;

import javafx.application.Platform;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Street implements Drawable {
    private final String street_name;
    private List<Coordinate> cords;
    private List<Stop> street_stops = null;
    private List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = new ArrayList<AbstractMap.SimpleImmutableEntry<Stop, Integer>> ();
    private final List<Shape> elements;
    private Boolean blocked;
    private List<Color> color_stack = new ArrayList<>(Arrays.asList(Color.BLACK));
    private boolean clicked;
    private VBox infoPane;
    private List<Line> street_lines = new ArrayList<>();
    protected Polyline line;
    private Integer delay_level = 0; // between 0 (min) and 4 (max)
//    private String type = "empty"; // now streets have a type (direction)

    public Street(String name) {
        this.street_name = name;
        this.cords = new ArrayList<>();
        this.street_stops = new ArrayList<>();
        this.elements = new ArrayList<>();
        this.blocked = false;
        this.infoPane = new VBox();
        if(name.equals("Street 2")){ // remove after level setter will be
            this.delay_level = 3;
        }
    }

//    public Street(Street street){
//        this.street_name = street.street_name;
//        this.cords = new ArrayList<>(street.cords);
//        this.street_stops = street.street_stops;
//        this.elements = street.elements;
//        this.blocked = street.blocked;
//        this.line = street.line;
//        this.stopLocation = street.stopLocation;
////        this.prev_color = street.prev_color;
////        this.street_lines = street.street_lines;
//    }

    public void setLine(Line newLine){
        if(!(this.street_lines.contains(newLine))){
            this.street_lines.add(newLine);
        }
    }

//    public void setType(String type){
//        this.type = type;
//    }
//    public String getType(){
//        return this.type;
//    }

    public List<Line> getLine(){
        return this.street_lines;
    }

    public Integer getDelayLevel(){
        return this.delay_level;
    }

    public Coordinate end() {
        List<Coordinate> lst = this.getCoordinates();
        return this.getCoordinates().get(lst.size() - 1);
    }

    public void setEnd(Coordinate new_end){
        this.getCoordinates().add(new_end);
    }

    public Coordinate begin() {
        List<Coordinate> lst = this.getCoordinates();
        return lst.get(0);
    }

    public void setBegin(Coordinate begin){
        this.getCoordinates().add(0,begin);
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

        street.line = new Polyline();
        for (Double point: points){
            street.line.getPoints().add(point);
        }
        street.line.setStrokeWidth(3);
        street.elements.add(street.line);

        if (stops != null){
            for (Stop stop : stops){
                boolean ok = street.addStop(stop);
                if (!ok){
                    System.out.println("Stop is not in the street, exit");
                    System.exit(1);
                }
            }
        }

        return street;
    }

    public String getId() {
        return this.street_name;
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

    @Override
    public void setInfo(MainController controller) {
//        this.createSideMenu(controller.getInfoContant());

        ContextMenu contextMenu = new ContextMenu();
        CheckMenuItem block = new CheckMenuItem("Bloked");
        Label label = new Label(this.getId());

        label.setVisible(false);
        label.setLabelFor(this.line);
        label.setStyle("-fx-background-color:POWDERBLUE");
        contextMenu.getItems().addAll(block);

        controller.getMapParent().getChildren().add(label);

        block.setSelected(false);
        final Paint[] prev_color = new Paint[1];
        block.setOnAction(event -> {
                    this.blocked = block.isSelected();
                    Street street = this;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (street.blocked){
                                prev_color[0] = street.line.getStroke();
                                street.line.setStroke(Color.RED);
                            }
                            else{
                                street.line.setStroke(prev_color[0]);
                            }
                        }
                    });
                    System.out.println("Street blocked? " + this.blocked);
                }
        );

        this.line.setOnContextMenuRequested(event -> {
            Street street = this;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    contextMenu.show(street.line, event.getScreenX(), event.getScreenY());
                }
            });
            System.out.println("Line clicked");
        });
        this.line.setOnMouseEntered(event -> {
            Street street = this;
            prev_color[0] = street.line.getStroke();
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    label.toFront();
                    label.setLayoutX(event.getSceneX() + 5);
                    label.setLayoutY(event.getSceneY() - 20);
                    label.setVisible(true);
                    street.line.setStroke(Color.BLUE);
                }
            });
        });
        this.line.setOnMouseClicked(event -> {
            if(this.clicked){
                this.clicked = false;
                controller.getInfo().toFront();
            }
            else {
                this.clicked = true;
                this.infoPane.toFront();
            }
        });

        this.line.setOnMouseExited(event -> {
            Street street = this;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    label.setVisible(false);
                    street.line.setStroke(prev_color[0]);
                }
            });
        });
    }

    private void createSideMenu(AnchorPane parent) {

        this.infoPane.setAccessibleHelp("All information about object");
        this.infoPane.setPrefSize(parent.getPrefWidth(), parent.getPrefHeight());
        this.infoPane.setStyle("-fx-background-color:yellow");

        TreeView<String> info = new TreeView<>();
        TreeItem<String> root = new TreeItem<>("Street info");
        Pane containerForTreeView = new Pane(info);

        this.infoPane.getChildren().add(containerForTreeView);
        this.infoPane.getChildren().add(new RadioButton("Blocked"));
        info.setPrefWidth(this.infoPane.getPrefWidth());
        info.setRoot(root);
        containerForTreeView.toBack();

        TreeItem<String> stops = new TreeItem<>("Street stops");
        if (this.street_stops.size() == 0){
            stops.setExpanded(true);
            stops.getChildren().add(new TreeItem<>("No stops"));
        }
        else{
            for (Stop stop: this.street_stops){
                stops.getChildren().add(new TreeItem<>(stop.getId()));
            }
        }
        root.getChildren().add(stops);
        root.setExpanded(true);
        parent.getChildren().add(this.infoPane);

    }

    public List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStopLocation() {
            return this.stopLocation;
    }

    public void changeLineColor(Color color) {
//        System.out.println("ALO");
//        this.prev_color = (Color) this.line.getStroke();
        this.color_stack.add(color);
        this.line.setStroke(this.color_stack.get(this.color_stack.size()-1));
    }
    public void rollBackLineColor(Color color){

//        this.line.setStroke(this.prev_color);
        this.color_stack.remove(color);
        this.line.setStroke(this.color_stack.get(this.color_stack.size()-1));
    }
}
