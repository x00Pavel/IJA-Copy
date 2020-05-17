/*
    Author: Pavel Yadlouski (xyadlo00)
            Oleksii Korniienko (xkorni02)

    File: src/functional/Street.java
    Date: 04.2020
 */


package src.functional;

import src.sample.MainController;
import src.sample.MenuController;
import src.Main;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementation of Street object.
 */
public class Street implements Drawable {
    private final String street_name;
    private final List<Coordinate> cords;
    private List<Stop> street_stops = null;
    private List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = new ArrayList<AbstractMap.SimpleImmutableEntry<Stop, Integer>>();
    private final List<Shape> elements;
    private Boolean blocked;
    private List<Color> color_stack = new ArrayList<>(Arrays.asList(Color.BLACK));
    private List<MyLine> street_lines = new ArrayList<>();
    protected Polyline line;
    private Integer delay_level = 0; // between 0 (min) and 4 (max)
    private AnchorPane infoPane;
    private MenuController controller;
    private boolean street_was_clicked = false;

    /**
     * Constructor for Street object
     * 
     * @param name Name of new street
     */
    public Street(String name) {
        this.street_name = name;
        this.cords = new ArrayList<>();
        this.street_stops = new ArrayList<>();
        this.elements = new ArrayList<>();
        this.blocked = false;
        this.delay_level = 0;
    }

    /**
     * State of street (was clicked or nor)
     *
     * @param new_clicked   New state
     */
    public void setStreetWasClicked(boolean new_clicked){
        this.street_was_clicked = new_clicked;
    }

    /**
     * Set a street`s lines
     *
     * @param newLine       New line, that will be added in list of street`s lines
     */
    public void setLine(MyLine newLine) {
        if (!(this.street_lines.contains(newLine))) {
            this.street_lines.add(newLine);
        }
    }

    /**
     * Get a street`s lines
     *
     * @return              List of street`s lines
     */
    public List<MyLine> getLine() {
        return this.street_lines;
    }

    /**
     * Get a street`s loading
     *
     * @return              Street`s loading level
     */
    public Integer getDelayLevel() {
        return this.delay_level;
    }

    /**
     * Set a street`s loading
     *
     * @param delay_level   New street`s loading level
     */
    public void setDelayLevel(Integer delay_level) {
        this.delay_level = delay_level;
    }

    /**
     * Return a coordinate of street`s end
     *
     * @return              Coordinate of street`s end
     */
    public Coordinate end() {
        List<Coordinate> lst = this.getCoordinates();
        return this.getCoordinates().get(lst.size() - 1);
    }

    /**
     * Return a coordinate of street`s begin
     *
     * @return              Coordinate of street`s begin
     */
    public Coordinate begin() {
        List<Coordinate> lst = this.getCoordinates();
        return lst.get(0);
    }

    /**
     * Check if actual street follows another one
     *
     * @param s             Street for check
     * @return              True if follows, false if not
     */
    public boolean follows(Street s) {
        return this.begin().equals(s.begin()) || this.begin().equals(s.end()) || this.end().equals(s.begin())
                || this.end().equals(s.end());
    }

    /**
     * Add a coordinate for street
     *
     * @param c             Coordinate for add
     */
    private void addCoord(Coordinate c) {
        if (!this.cords.contains(c)) {
            this.cords.add(c);
        }
    }

    /**
     * Check if street`s angles are rights
     *
     * @param num           List of parts of streets
     * @return              True if angles was right, false if not
     */
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

    /**
     * Create default street
     *
     * @param id          Name (ID) of new street
     * @param coordinates List of coordinates
     * @param stops       List of stops on street
     * @return New instance of Street object
     */
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
            points.addAll(Arrays.asList((double) coordinates.get(0).getX(), (double) coordinates.get(0).getY(),
                    (double) coordinates.get(1).getX(), (double) coordinates.get(1).getY()));
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
                    points.addAll(Arrays.asList((double) a.getX(), (double) a.getY(), (double) b.getX(),
                            (double) b.getY(), (double) c.getX(), (double) c.getY()));
                } else {
                    return null;
                }

            }
        }

        street.line = new Polyline();
        for (Double point : points) {
            street.line.getPoints().add(point);
        }
        street.line.setStrokeWidth(3);
        street.elements.add(street.line);

        if (stops != null) {
            for (Stop stop : stops) {
                boolean ok = street.addStop(stop);
                if (!ok) {
                    System.out.println("Stop is not in the street, exit");
                    System.exit(1);
                }
            }
        }

        return street;
    }

    /**
     * Get a street`s name(id)
     *
     * @return              Name(id) of street
     */
    public String getId() {
        return this.street_name;
    }

    /**
     * Get a list of street`s stops
     *
     * @return              List of street`s stops
     */
    public List<Stop> getStops() {
        return this.street_stops;
    }

    /**
     * Add stop to street
     *
     * @param stop Stop to be added
     * @return True if adding in completed succesfully, else false
     */
    public boolean addStop(Stop stop) {
        Coordinate coord = stop.getCoordinate();
        List<Coordinate> lst = this.getCoordinates();
        for (int i = 0; i < lst.size(); i++) {
            Coordinate first = lst.get(i);
            Coordinate second = lst.get(i + 1);
            int first_coord_x = (int) (Math.pow(first.diffX(coord), 2));
            int first_coord_y = (int) (Math.pow(first.diffY(coord), 2));
            int first_coord_z = first_coord_x + first_coord_y;

            int second_coord_x = (int) (Math.pow(second.diffX(coord), 2));
            int second_coord_y = (int) (Math.pow(second.diffY(coord), 2));
            int second_coord_z = second_coord_x + second_coord_y;

            int second_first_x = (int) (Math.pow(second.diffX(first), 2));
            int second_first_y = (int) (Math.pow(second.diffY(first), 2));
            int second_first_z = second_first_x + second_first_y;

            if ((int) Math.sqrt(second_coord_z) + (int) Math.sqrt(first_coord_z) == (int) Math.sqrt(second_first_z)) {
                stop.setStreet(this);
                this.street_stops.add(stop);
                AbstractMap.SimpleImmutableEntry<Stop, Integer> e = new AbstractMap.SimpleImmutableEntry<>(stop, i);
                stopLocation.add(e);
                return true;
            }
            if (i + 2 == lst.size()) {
                break;
            }

        }
        return false;
    }

    @Override
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

    /**
     * Get list of street coordinates
     *
     * @return List of Coordinate objects
     */
    public List<Coordinate> getCoordinates() {
        return this.cords;
    }

    @Override
    public List<Shape> getGUI() {
        return this.elements;
    }

    /**
     * Set interactiv activity for given street
     *
     * @param mainController Main controller of scene
     */
    @Override
    public void setInfo(MainController mainController) {
        this.createSideMenu(mainController.getInfoContant());

        Label label = new Label(this.getId());

        label.setVisible(false);
        label.setLabelFor(this.line);
        label.setStyle("-fx-background-color:POWDERBLUE");

        // Set label for parent element for correct showing on scene
        mainController.getMapParent().getChildren().add(label);

        this.line.setOnMouseEntered(event -> {
            Street street = this;
            street.changeLineColor(Color.BLUE);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    label.toFront();
                    label.setLayoutX(event.getSceneX() + 5);
                    label.setLayoutY(event.getSceneY() - 20);
                    label.setVisible(true);
                    System.out.println( street.getId() + " - " +  event.getX() + ":" + event.getY());
                }
            });
        });
        this.line.setOnMouseClicked(event -> { // click on street
            if (Main.controller.getMode().equals("default")) {
                int size = mainController.getInfoContant().getChildren().size();
                if (mainController.getInfoContant().getChildren().get(size - 1).getId().equals("streetMenu")){
                    mainController.showMainMenu();
                }
                else {
                    mainController.deselectObjects();
                    mainController.showStreetMenu(this.infoPane);
                }
            } else {
                List<Bus> list_buses = Main.controller.getListBuses();
                List<Bus> buses_need = new ArrayList<>();
                System.out.println("Mode is EDIT!");
                for (Bus bus : list_buses) {
                    if (bus.getSpeed() == 0) {
                        buses_need.add(bus);
                    }
                }
                for(Street street: Main.controller.getListStreets()){
                    if(!street.equals(this)){
                        street.setStreetWasClicked(false);
                    }
                }
                Bus bus = buses_need.get(0);

                MyLine bus_line = bus.getBusLine();

                this.rollBackLineColor(Color.LIGHTGRAY);

                if(bus_line.getPaintedStreet().contains(this) || bus_line.getStreets().contains(this)){

                    if(this.street_was_clicked){
                        List<Stop> temp_stops_for_back = new ArrayList<>();
                        for (Stop stop : this.getStops()) {
                            if (bus_line.getStreetsTypes().get(this.getId()).equals("back")) {
                                temp_stops_for_back.add(stop);
                            } else {
                                bus_line.getTempNewStops().add(stop);
                            }
                        }
                        while (!temp_stops_for_back.isEmpty()) {
                            bus_line.getTempNewStops().add(temp_stops_for_back.remove(temp_stops_for_back.size() - 1));
                        }
                        this.street_was_clicked = false;
                        System.out.println("All stops was added!");
                    }else{
                        this.street_was_clicked = true;

                    bus_line.getTempNewStreet().add(this);
                    for (Street new_street : bus_line.getPaintedStreet()) {
                        if(!new_street.equals(this)){
                            new_street.rollBackLineColor(bus.getColor());
                        }
                    }

                    showNextRoads(this, bus_line, bus);

                    Street last_street = null;
    
                    if (bus.getStreetForContinue().equals(this)) {
                        last_street = this;
                    }
    
                    if (last_street != null) {
                        System.out.println("NEW ROAD WAS CREATED!");
                        last_street.setStreetWasClicked(false);
                        last_street.color_stack = new ArrayList<>(Arrays.asList(Color.BLACK));

                        for(Street street: bus_line.getPaintedStreet()){
                            street.rollBackLineColor(bus.getColor());
                        }

                        List<Stop> new_stops_for_line = new ArrayList<>();

                        for(Stop stop: bus.getStreetForContinue().getStops()){
                            if(bus.getBusLine().getStops().contains(stop)){
                                new_stops_for_line.add(stop);
                            }
                        }
                        new_stops_for_line.addAll(bus_line.getTempNewStops());
                        bus_line.setNewStops(new_stops_for_line);

                        for (Stop stop: new_stops_for_line){
                            stop.addBus(bus, bus_line.getStopsTimes().get(stop.getId()));
                        }

                        List<Street> new_streets_for_line = new ArrayList<>();
                        new_streets_for_line.add(bus.getStreetForContinue());

                        for (Street new_street_in_line : bus_line.getTempNewStreet()) {
                            if (!new_streets_for_line.contains(new_street_in_line)) {
                                new_streets_for_line.add(new_street_in_line);
                            }
                        }
                        bus_line.setNewStreets(new_streets_for_line);
    
                        for (Stop stop : bus_line.getStops()) {
                            if (!bus_line.getStopsTimes().containsKey(stop.getId())) {
                                bus_line.addStopsTimes(stop.getId(), 0, 0);
                                bus_line.addStopsFlags(stop.getId(), 0);
                            }
                        }
                        bus.continueBus();
    
                        buses_need.remove(0);
                        if(!buses_need.isEmpty()){
                            bus_line.getTempNewStreet().clear();
                            bus_line.getTempNewStops().clear();

                            bus = buses_need.get(0);
                            bus_line = bus.getBusLine();
                            int temp_blocked_street_index = bus_line.getStreets().indexOf(bus_line.getBlockedStreet());
                            if(temp_blocked_street_index == 0){
                                temp_blocked_street_index = temp_blocked_street_index + bus_line.getStreets().size();
                            }
                            Street first_street = bus_line.getStreets().get(temp_blocked_street_index-1);
                            showNextRoads(first_street, bus_line, bus);
                        }else{
                            Main.controller.changeMode("default");
                            bus_line.getTempNewStreet().clear();
                            bus_line.getTempNewStops().clear();
                        }
                    }
                }
                }else{
                    System.out.println("You can`t choose this street!");
                }
            }
        });

        this.line.setOnMouseExited(event -> {
            Street street = this;
            street.rollBackLineColor(Color.BLUE);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    label.setVisible(false);
                }
            });
        });
    }

    /**
     * Set block property for Street
     *
     * @param block Boolean value, True to bloc street, False to unblock
     */
    private void setBlock(boolean block) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (block) {
                    Street.this.changeLineColor(Color.LIGHTGRAY);
                    Street.this.controller.setStreetBlock(true);
                    Main.controller.changeMode("edit");

                    List<Bus> buses_need = new ArrayList<>();
                    for (Bus bus : Main.controller.getListBuses()) {
                        if (bus.getBusLine().getStreets().contains(Street.this)) {
                            buses_need.add(bus);
                        }
                    }

                    for(Bus bus: buses_need){
                        bus.getBusLine().setBlockedStreet(Street.this);
                        int start_street_index = bus.getBusLine().getStreets().indexOf(Street.this)-1;
                        if(start_street_index < 0){
                            start_street_index = start_street_index + bus.getBusLine().getStreets().size();
                        }
                        bus.setStreetForContinue(bus.getBusLine().getStreets().get(start_street_index));
                    }

                    if(!buses_need.isEmpty()){
                        MyLine bus_line = buses_need.get(0).getBusLine();
                        int street_for_continue_index = bus_line.getStreets().indexOf(Street.this)-1;
                        if(street_for_continue_index < 0){
                            street_for_continue_index = street_for_continue_index + bus_line.getStreets().size();
                        }
                        buses_need.get(0).setStreetForContinue(bus_line.getStreets().get(street_for_continue_index));
    
                        showNextRoads(buses_need.get(0).getStreetForContinue(), buses_need.get(0).getBusLine(), buses_need.get(0));
                    }else{
                        Street.this.controller.setStreetBlock(true);
                        Main.controller.changeMode("default");
                    }
                }
            }
        });
    }

    /**
    * @brief Set side menu bar for street information
    *
    * @param parent Container for menu
     */
    private void createSideMenu(AnchorPane parent) {
        FXMLLoader sideLoader = new FXMLLoader(getClass().getResource("sideMenu.fxml"));
        try {
            this.infoPane = sideLoader.load();
        } catch (IOException e) {
            e.getMessage();
        }

        controller = sideLoader.getController();
        controller.setStreet(this);
        controller.setStreetNameField(this.getId());

        // Set action for load buttons
        controller.getStreetLoadingMinus().setOnAction(event -> {
            if (this.getDelayLevel() > 0){
                this.setDelayLevel(this.getDelayLevel() - 1);
                controller.setStreetLoading(String.valueOf(this.getDelayLevel()));
            }
        });

        controller.getStreetLoadingPlus().setOnAction(event -> {
            if (this.getDelayLevel() < 4){
                this.setDelayLevel(this.getDelayLevel() + 1);
                controller.setStreetLoading(String.valueOf(this.getDelayLevel()));
            }
        });
        controller.getStreetBlock().setOnMouseClicked(event -> { // click block checkbox
            List<Bus> list_buses = Main.controller.getListBuses();
            boolean bus_on_street = false;
            for(Bus bus: list_buses){
                if(bus.getActualBusStreet().getId().equals(this.getId())){
                    System.out.println("Bus in blocked street will end his actual road!");
                    break;
                }
            }

            if(!bus_on_street){
                this.setBlock(controller.getStreetBlock().isSelected());
                controller.setStreetBlock(this.blocked);
                for(Bus bus: list_buses){
                    if(bus.getBusLine().getStreets().contains(this)){
                        if(Main.controller.getMode() == "default"){
                            if(bus.getSpeed() == 0){
                                bus.continueBus();
                            }else{
                                bus.pauseBus();
                                bus.setRestartFlag();
                            }
                        }
                    }
                }
            }
        });

        AnchorPane.setBottomAnchor(this.infoPane, 0.0);
        AnchorPane.setLeftAnchor(this.infoPane, 0.0);
        AnchorPane.setRightAnchor(this.infoPane, 0.0);
        AnchorPane.setTopAnchor(this.infoPane, 0.0);
        TreeView<String> info = controller.getInfo();
        TreeItem<String> root = new TreeItem<>("Street info");

        info.setRoot(root);

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

        TextField streetLoading = controller.getStreetLoading();

        streetLoading.setText(String.valueOf(this.delay_level));
        parent.getChildren().add(this.infoPane);

    }

    /**
     * Get a street`s stop`s location
     *
     * @return              List of pair stop`s name and the part of the road on which the stop is located
     */
    public List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStopLocation() {
            return this.stopLocation;
    }

    /**
     * Change a color of all streets in this line
     *
     * @param color         Color for change
     */
    public void changeLineColor(Color color) {
        this.color_stack.add(color);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Street.this.line.setStroke(Street.this.color_stack.get(Street.this.color_stack.size()-1));
            }
        });
    }

    /**
     * Change a color of all streets in this line to previously in half-stack
     *
     * @param color         Color for change and delete from half-stack
     */
    public void rollBackLineColor(Color color){
        this.color_stack.remove(color);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Street.this.line.setStroke(Street.this.color_stack.get(Street.this.color_stack.size()-1));
            }
        });
    }

    /**
     * Paint a streets can be choosed
     *
     * @param street_start  Street for begin painting
     * @param bus_line      Line for painting
     */
    public void showNextRoads(Street street_start, MyLine bus_line, Bus bus){
        if(street_start.equals(bus.getStreetForContinue())){
            street_start.changeLineColor(Color.DEEPSKYBLUE);
        }
        String street_type;
        List<Street> painted_streets = bus_line.getPaintedStreet();
        street_type = bus_line.getStreetsTypes().get(street_start.getId());
        List<Street> all_streets = Main.controller.getListStreets();

        Coordinate end_coord;
        if (street_type.equals("back")) {
            end_coord = street_start.begin();
        } else {
            end_coord = street_start.end();
        }

        for (Street street : all_streets) {
            if (!street.equals(street_start)) {
                if (end_coord.equals(street.begin())){
                    street.changeLineColor(bus.getColor());
                    painted_streets.add(street);
                    bus_line.addStreetType(street.getId(), "forward");
                }else if (end_coord.equals(street.end())){
                    street.changeLineColor(bus.getColor());
                    painted_streets.add(street);
                    bus_line.addStreetType(street.getId(), "back");
                }
            }
        }
    }
}
