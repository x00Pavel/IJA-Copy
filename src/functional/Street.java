/**
 * File: ija/src/functional/Street.java
 * 
 * Author: Pavel Yadlouski (xyadlo00)
 *         Oleksii Korniienko (xkorni02)
 * 
 * Date: 04.2020
 * 
 * Description: Implementation of Street object with its functionality
 */

package src.functional;

import javafx.util.Pair;
import src.sample.MainController;
import src.sample.MenuController;
import src.Main;

import javafx.application.Platform;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;

import javafx.scene.control.Label;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class Street implements Drawable {
    private final String street_name;
    private final List<Coordinate> cords;
    private List<Stop> street_stops = null;
    private List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stopLocation = new ArrayList<AbstractMap.SimpleImmutableEntry<Stop, Integer>> ();
    private final List<Shape> elements;
    private Boolean blocked;
    private List<Color> color_stack = new ArrayList<>(Arrays.asList(Color.BLACK));
    private boolean clicked;
    private List<Line> street_lines = new ArrayList<>();
    protected Polyline line;
    private Integer prev_delay_level = 0;
    private Integer delay_level = 0; // between 0 (min) and 4 (max)
    private AnchorPane infoPane;
    private MenuController controller;
//    private String type = "empty"; // now streets have a type (direction)


    public Street(String name) {
        this.street_name = name;
        this.cords = new ArrayList<>();
        this.street_stops = new ArrayList<>();
        this.elements = new ArrayList<>();
        this.blocked = false;
        this.delay_level = 0;
    }

    public void setLine(Line newLine){
        if(!(this.street_lines.contains(newLine))){
            this.street_lines.add(newLine);
        }
    }

    public List<Line> getLine(){
        return this.street_lines;
    }

    public Integer getPrevDelayLevel(){
        return this.prev_delay_level;
    }

    public void setPrevDelayLevel(Integer prev_delay_level) {
        this.prev_delay_level = prev_delay_level;
    }

    public Integer getDelayLevel(){
        return this.delay_level;
    }

    public void setDelayLevel(Integer delay_level) {
        this.delay_level = delay_level;
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

    /**
     * Create default street
     *
     * @param id Name (ID) of new street
     * @param coordinates List of coordinates
     * @param stops List of stops on street
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
     * @brief Set interactiv activity for given street
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

        // final Paint[] prev_color = new Paint[1];

        this.line.setOnMouseEntered(event -> {
            Street street = this;
            // prev_color[0] = street.line.getStroke();
            street.changeLineColor(Color.BLUE);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    label.toFront();
                    label.setLayoutX(event.getSceneX() + 5);
                    label.setLayoutY(event.getSceneY() - 20);
                    label.setVisible(true);
                    // street.line.setStroke(Color.BLUE);
                }
            });
        });
        this.line.setOnMouseClicked(event -> { // click on street
                                               // ------------------------------------------------------------
            if (Main.controller.getMode() == "default") {
                if (this.clicked) {
                    this.clicked = false;
                    controller.getInfo().toFront();
                } else {
                    this.clicked = true;
                    this.infoPane.toFront();
                }
            } else {
                List<Pair<ExecutorService, List<Bus>>> list_lines = Main.controller.getListLines();
                List<Bus> buses_need = new ArrayList<>();
                for(Pair<ExecutorService, List<Bus>> pair: list_lines){
                    for (Bus bus : pair.getValue()){
                        if (bus.getSpeed() == 0) {
                            buses_need.add(bus);
                        }
                    }
                }
                System.out.println("Mode is EDIT!");
                Bus bus = buses_need.get(0);

                Line bus_line = bus.getBusLineForUse();

                bus_line.getTempNewStreet().add(this);
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

                showNextRoads(this, bus_line, bus);

                Street last_street = null;

                if (bus_line.getStreets().contains(this)) {
                    last_street = this;
                }

                // for(Street street: bus_line.getStreets()){
                // if(street.follows(this) && bus_line.getTempNewStreet().size()>1){
                // last_street = street;
                // }
                // }

                if (last_street != null) {
                    System.out.println("NEW ROAD WAS CREATED!");
                    System.out.println(bus_line.getTempNewStreet());
                    for (Street new_street : bus_line.getPaintedStreet()) {
                        new_street.rollBackLineColor(bus.getColor());
                    }

                    int blocked_street_index = bus_line.getStreets().indexOf(bus_line.getBlockedStreet());
                    if (blocked_street_index < 0) {
                        blocked_street_index = blocked_street_index + bus_line.getStreets().size();
                    }
                    Street street_for_start = bus_line.getStreets().get(blocked_street_index - 1);
                    Street street_for_end = last_street;

                    Stop stop_for_start = findStopForStart(bus_line, street_for_start);

                    int stop_for_start_index = 0;
                    if (stop_for_start != null) {
                        stop_for_start_index = bus_line.getStops().indexOf(stop_for_start);
                    }

                    Stop stop_for_end = findStopForEnd(bus_line, street_for_end);

                    int stop_for_end_index = -1;
                    if (stop_for_end != null) {
                        stop_for_end_index = bus_line.getStops().indexOf(stop_for_end);
                    }

                    List<Stop> new_stops_for_line = new ArrayList<>();

                    for (int i = 0; i <= stop_for_start_index; i++) {
                        new_stops_for_line.add(bus_line.getStops().get(i));
                    }
                    for (Stop new_stop_in_line : bus_line.getTempNewStops()) {
                        new_stops_for_line.add(new_stop_in_line);
                    }
                    for (int i = stop_for_end_index + 1; i < bus_line.getStops().size(); i++) {
                        new_stops_for_line.add(bus_line.getStops().get(i));
                    }

                    bus_line.setNewStops(new_stops_for_line);
                    // -----------------------------------------------------------------------------------------------------------------------

                    int street_for_start_index = blocked_street_index - 1;
                    int street_for_end_index = bus_line.getStreets().indexOf(last_street);

                    List<Street> new_streets_for_line = new ArrayList<>();
                    for (int i = 0; i <= street_for_start_index; i++) {
                        new_streets_for_line.add(bus_line.getStreets().get(i));
                    }
                    for (Street new_street_in_line : bus_line.getTempNewStreet()) {
                        if (!new_streets_for_line.contains(new_street_in_line)) {
                            new_streets_for_line.add(new_street_in_line);
                        }
                    }
                    if (street_for_start_index < street_for_end_index) {
                        for (int i = street_for_end_index + 1; i < bus_line.getStreets().size(); i++) {
                            new_streets_for_line.add(bus_line.getStreets().get(i));
                        }
                    }

                    bus_line.setNewStreets(new_streets_for_line);

                    for (Stop stop : bus_line.getStops()) {
                        // tempLine.createOriginalStopsTimes(stop.getId(), 0);
                        if (!bus_line.getStopsTimes().containsKey(stop.getId())) {
                            bus_line.addStopsTimes(stop.getId(), 0, 0);
                            bus_line.addStopsFlags(stop.getId(), 0);
                        }
                    }

                    System.out.println("Streets: " + bus.getBusLineForUse().getStreets());
                    System.out.println("Streets types: " + bus.getBusLineForUse().getStreetsTypes());

                    // bus.setRestartFlag();

                    bus.continueBus();
                    // bus.setOldTimeForRing(bus.getTimeForRing());

                    buses_need.remove(0);
                    if(!buses_need.isEmpty()){
                        bus = buses_need.get(0);
                        bus_line = bus.getBusLineForUse();
                        Street first_street = bus_line.getStreets().get(bus_line.getStreets().indexOf(bus_line.getBlockedStreet())-1);
                        showNextRoads(first_street, bus_line, bus);
                    }else{
                        Main.controller.changeMode("default");
                        // System.out.println("blocked_street_colors: " + this.color_stack);
                        this.rollBackLineColor(Color.LIGHTGRAY); // mb need to change
                        // System.out.println("this: " + this.color_stack);
                        // System.out.println("this: " + this.color_stack.size());
                    }
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
                    // street.line.setStroke(prev_color[0]);
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
        // final Paint[] prev_color = new Paint[1];
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (block) {
                    Street.this.changeLineColor(Color.LIGHTGRAY);
                    Street.this.controller.setStreetBlock(true);
                    Main.controller.changeMode("edit");

                    List<Pair<ExecutorService, List<Bus>>> list_lines = Main.controller.getListLines();
                    List<Bus> buses_need = new ArrayList<>();
                    for(Pair<ExecutorService, List<Bus>> pair: list_lines){
                        for (Bus bus : pair.getValue()){
                            if (bus.getSpeed() == 0) {
                                buses_need.add(bus);
                            }
                        }
                    }

                    for(Bus bus: buses_need){
                        bus.getBusLineForUse().setBlockedStreet(Street.this);
                    }

                    Line bus_line = buses_need.get(0).getBusLineForUse();
                    int street_for_continue_index = bus_line.getStreets().indexOf(Street.this)-1;
                    if(street_for_continue_index < 0){
                        street_for_continue_index = street_for_continue_index + bus_line.getStreets().size();
                    }
                    Street street_for_continue = bus_line.getStreets().get(street_for_continue_index);

                    showNextRoads(street_for_continue, buses_need.get(0).getBusLineForUse(), buses_need.get(0));
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
            if (this.getDelayLevel() > 1){
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
        controller.getStreetBlock().setOnMouseClicked(event -> { //---------------------------------------------------------------------------------------------------
            for(Pair<ExecutorService, List<Bus>> pair: Main.controller.getListLines()) {
            boolean bus_on_street = false;
                for(Bus bus: pair.getValue()){
                int bus_street_x_begin = bus.getActualBusStreet().begin().getX();
                int bus_street_y_begin = bus.getActualBusStreet().begin().getY();
                int bus_street_x_end = bus.getActualBusStreet().end().getX();
                int bus_street_y_end = bus.getActualBusStreet().end().getY();
                if(bus.getActualBusStreet().getId().equals(this.getId())){
                    if((Math.round(bus.getBusX()) == bus_street_x_begin && Math.round(bus.getBusY()) == bus_street_y_begin) || (Math.round(bus.getBusX()) == bus_street_x_end && Math.round(bus.getBusY()) == bus_street_y_end)){

                    }else{
                        // System.out.println("Actual street: " + bus.getActualBusStreet().getId());
                        // System.out.println("Bus X: " + Math.round(bus.getBusX()));
                        // System.out.println("Bus Y: " + Math.round(bus.getBusY()));
                        // System.out.println("bus_street_x_begin: " + bus_street_x_begin);
                        // System.out.println("bus_street_y_begin: " + bus_street_y_begin);
                        // System.out.println("bus_street_x_end: " + bus_street_x_end);
                        // System.out.println("bus_street_y_end: " + bus_street_y_end);
                        System.out.println("Bus in blocked street will goes back!");
                        // bus_on_street = true;
                        bus.setGoBack();
                        //need to delete "galochka" from box
                        break;
                    }
                }
            }

            if(!bus_on_street){
                this.setBlock(controller.getStreetBlock().isSelected());
                controller.setStreetBlock(this.blocked);
                    for(Bus bus: pair.getValue()){
                    if(bus.getBusLineForUse().getStreets().contains(this)){
                        // int bus_index = list_buses.indexOf(bus);
                        // Main.controller.getBusesThread().get(bus_index).interrupt();
                        if(Main.controller.getMode() == "default") {
                            if (bus.getSpeed() == 0) {
                                bus.continueBus();
                            } else {
                                bus.pauseBus();
                                bus.setRestartFlag();
                            }
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

    public List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> getStopLocation() {
            return this.stopLocation;
    }

    /**
     * Change color of street to given
     * @param color New color of street
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
     * Change color of street to previous
     * @param color Color to be removed from color stack
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

    public void showNextRoads(Street street_start, Line bus_line, Bus bus){
        String street_type;
        List<Street> painted_streets = bus_line.getPaintedStreet();
        street_type = bus_line.getStreetsTypes().get(street_start.getId());
        List<Street> all_streets = Main.controller.getListStreets();

        System.out.println("street_start: " + street_start.getId());
        System.out.println("street_start type: " + street_type);

        Coordinate end_coord;
        if (street_type.equals("back")) {
            end_coord = street_start.begin();
        } else {
            end_coord = street_start.end();
        }

        // System.out.println("start_street_name: " + street_start.getId() + "start_coord: " + start_coord);

        for (Street street : all_streets) {
            // System.out.println("start_name: " + street.getId() + " street_begin: " + street.begin() + " street_end: " + street.end());
            if ((!bus_line.getStreets().contains(street) && !street.equals(street_start)) || (street.equals(bus_line.getBlockedStreet()) && !street.equals(street_start))) {
                if (end_coord.equals(street.begin())){
                    street.changeLineColor(bus.getColor());
                    painted_streets.add(street);
                    bus_line.addStreetType(street.getId(), "forward");
                    // System.out.println("Street: " + street.getId());
                    // System.out.println("Street type: " + bus_line.getStreetsTypes().get(street.getId()));
                }else if (end_coord.equals(street.end())){
                    street.changeLineColor(bus.getColor());
                    painted_streets.add(street);
                    bus_line.addStreetType(street.getId(), "back");
                    // System.out.println("Street: " + street.getId());
                    // System.out.println("Street type: " + bus_line.getStreetsTypes().get(street.getId()));
                }
            }
        }
    }

    private Stop findStopForStart(Line bus_line, Street street_for_start){
        Stop stop_for_start = null;

        List<Stop> temp_stops = new ArrayList<>();

        for(Stop stop:bus_line.getStops()){
            if(street_for_start.getStops().contains(stop)){
                temp_stops.add(stop);
            }
        }

        if(temp_stops.isEmpty()){

            int new_street_for_start_index = bus_line.getStreets().indexOf(street_for_start)-1;

            if(new_street_for_start_index < 0){
                return null;
            }

            Street new_street_for_start = bus_line.getStreets().get(new_street_for_start_index);

            stop_for_start = findStopForStart(bus_line, new_street_for_start);
        }else{
            for(Stop stop:temp_stops){
                stop_for_start = stop;
            }
        }
        return stop_for_start;
    }

    private Stop findStopForEnd(Line bus_line, Street street_for_end){
        Stop stop_for_end = null;

        List<Stop> temp_stops = new ArrayList<>();

        for(Stop stop:bus_line.getStops()){
            if(street_for_end.getStops().contains(stop)){
                temp_stops.add(stop);
            }
        }

        if(temp_stops.isEmpty()){

            int new_street_for_end_index = bus_line.getStreets().indexOf(street_for_end)+1;

            if(new_street_for_end_index >= bus_line.getStops().size()){
                return null;
            }

            Street new_street_for_end = bus_line.getStreets().get(new_street_for_end_index);

            stop_for_end = findStopForEnd(bus_line, new_street_for_end);
        }else{
            stop_for_end = temp_stops.get(0);
        }
        return stop_for_end;
    }
}
