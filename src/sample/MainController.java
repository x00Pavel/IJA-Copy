/*
    Author: Pavel Yadlouski (xyadlo00)
            Oleksii Korniienko (xkorni02)

    File: src/sample/MainController.java
    Date: 04.2020
 */


package src.sample;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import src.Main;
import src.functional.*;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.fxml.FXML;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;


/**
 * Controller of main scene.
 */
public class MainController{

    private List<Drawable> elements = new ArrayList<>();

    private List<Stop> list_stops = new ArrayList<>();
    private List<Street> list_streets = new ArrayList<>();
    private List<Bus> list_buses = new ArrayList<>();
    private Clock clock;
    private String mode = "default";
    private Bus lineClicked;


    @FXML
    private AnchorPane mainInfo;

    @FXML
    private AnchorPane stopMenu;

    @FXML
    private TextField stopNameField;

    @FXML
    private VBox bussesBox;

    @FXML
    private Button startButton;

    @FXML
    private Pane content;

    @FXML
    private TextField clockField;

    @FXML
    private TextField timeSpeedField;

    @FXML
    private TextField scaleField;

    @FXML
    private AnchorPane mapParent;

    @FXML
    private AnchorPane infoContant;

    @FXML
    private AnchorPane busMenu;

    @FXML
    private TreeView<String> busTreeView;

    @FXML
    private TextField busNameField;

    @FXML
    private TreeView<String> info;

    /**
     * Get container of side Stop menu
     *
     * @return Anchor pane where side menu should be placed
     */
    public AnchorPane getStopMenu() {
        return stopMenu;
    }

    /**
     * Get object with Stop
     * 
     * @return Text field
     */
    public TextField getStopNameField() {
        return stopNameField;
    }

    /**
     * Get tree view in side menu of Bus
     *
     * @return Tree view
     */ 
     public TreeView<String> getBusTreeView() {
        return busTreeView;
    }

    /**
     * Get object where bus name should be shown
     * 
     * @return text field of name
     */
    public TextField getBusNameField() {
        return busNameField;
    }

    /**
     * Get container with main side menu
     * 
     * @return Container with main side menu
     */
    public AnchorPane getMainInfo() {
        return mainInfo;
    }

    /**
     * Getting text field where clock is shown
     * @return TextField of clock
     */
    public TextField getClockObj(){
        return clockField;
    }

    /**
     * Get parent of map to putting labels on it
     * @return Parent of map
     */
    public AnchorPane getMapParent() {
        return mapParent;
    }

    /**
     * Getting list of all street on map
     *
     * @return List of Street object
     */
    public List<Street> getListStreets(){
        return this.list_streets;
    }

    /**
     * Getting list of all buses running on map
     * @return List of Bus object
     */
    public List<Bus> getListBuses(){
        return this.list_buses;
    }

    /**
     * Changing mode of application
     * @param new_mode New mode
     */
    public void changeMode(String new_mode){
        this.mode = new_mode;
    }

    /**
     * Get mode of application: default or edit
     * @return String with mode
     */
    public String getMode(){
        return this.mode;
    }

    /**
     * Adding given elements to scene
     *
     * @param elements List of graphical elements
     */
    public void setElements(List<Drawable> elements) {
        this.elements = elements;
        for(Drawable item: elements){
            content.getChildren().addAll(item.getGUI());
        }
    }

    /**
     * Build streets and stops on map
     *
     * @param file XML file with map definition
     * @return List of graphical elements to be drawn on map
     */
    @FXML
    public List<Drawable> buildMap(File file) throws IOException {
        for (int i = 0; i < 1000; i += 10){
            Line line = new Line(i, 0, i, 1000);
            line.setStroke(Color.LIGHTGRAY);
            Line hor = new Line(0, i, 1000, i);
            hor.setStroke(Color.LIGHTGRAY);
            content.getChildren().addAll(line, hor);
        }
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            Element root  = doc.getDocumentElement();

            NodeList streets = root.getElementsByTagName("Street");

            for (int temp = 0; temp < streets.getLength() ; temp++) {
                List<Coordinate> street_cords = new ArrayList<>();
                List<Stop> street_stops = new ArrayList<>();

                Element street = (Element) streets.item(temp);
                NodeList coords = street.getElementsByTagName("Coordinates").item(0).getChildNodes();
                for (int i = 0; i < coords.getLength(); i++){
                    if(coords.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element tmp = (Element) coords.item(i);
                        int x = Integer.parseInt(tmp.getAttribute("x"));
                        int y = Integer.parseInt(tmp.getAttribute("y"));
                        street_cords.add(new Coordinate(x, y));
                    }
                }
                Street new_street = null;
                try {
                    NodeList stops = street.getElementsByTagName("Stops").item(0).getChildNodes();
                    for (int i = 0; i < stops.getLength(); i++) {
                        if (stops.item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element tmp = (Element) stops.item(i);
                            String stopName = tmp.getAttribute("name");
                            NodeList tmp_node = tmp.getChildNodes();
                            for (int j = 0; j < tmp_node.getLength(); j++) {
                                if (tmp_node.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                    Element tmp_coord = (Element) tmp_node.item(j);
                                    int x = Integer.parseInt(tmp_coord.getAttribute("x"));
                                    int y = Integer.parseInt(tmp_coord.getAttribute("y"));
                                    Stop tmp_stop = new Stop(stopName, new Coordinate(x, y));
                                    street_stops.add(tmp_stop);

                                    list_stops.add(tmp_stop);
                                }
                            }
                        }
                    }
                    new_street = Street.defaultStreet(street.getAttribute("name"), street_cords, street_stops);
                    elements.add(new_street);
                    for (Stop stop : street_stops){
                        stop.setInfo(this);
                        elements.add(stop);
                    }

                }
                catch (NullPointerException e){
                    new_street = Street.defaultStreet(street.getAttribute("name"), street_cords, null);
                    elements.add(new_street);
                }
                new_street.setInfo(this);
                list_streets.add(new_street);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return elements;
    }

    /**
     * Zoom map based on mouse scroll
     *
     * @param event Scroll event from mouse
     */
    @FXML
    private void onScroll(ScrollEvent event){
        // If this would not be set, then zooming would be propagate to parent elements
        event.consume();
        double zoom = event.getDeltaY() > 0 ? 1.1 : 0.9;
        content.setScaleX(zoom * content.getScaleX());
        content.setScaleY(zoom * content.getScaleY());
        content.layout();
    }

    /**
     * Bring closer map on button clicked
     * @param event Button clicked event
     */
    @FXML
    private void makeBigger(ActionEvent event){
        if (Integer.parseInt(scaleField.getText().replace("%","")) < 190){
            event.consume();
            double zoom = 1.1;
            content.setScaleX(zoom * content.getScaleX());
            content.setScaleY(zoom * content.getScaleY());
            content.layout();
            scaleField.setText((Integer.parseInt(scaleField.getText().replace("%", "")) + 10) +"%");
        }
    }

    /**
     * Reduce map on button clicked
     * @param event Button clicked event
     */
    @FXML
    private void makeSmaller(ActionEvent event){
        if (Integer.parseInt(scaleField.getText().replace("%","")) > 90){
            event.consume();
            double zoom = 0.9;
            content.setScaleX(zoom * content.getScaleX());
            content.setScaleY(zoom * content.getScaleY());
            content.layout();
            scaleField.setText((Integer.parseInt(scaleField.getText().replace("%", "")) - 10) +"%");
        }
    }

    /**
     * Creating of bus lines
     *
     * @param file XML file with definition of bus lines
     * @return List of new buses
     */
    @FXML
    public List<Bus> buildLines(File file){

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();

            NodeList lines = root.getElementsByTagName("Line");

            for (int temp = 0; temp < lines.getLength() ; temp++) {

                Element line = (Element) lines.item(temp);
                String lineName = line.getAttribute("name");
                String busColor = line.getAttribute("color");
                int time_for_ring = Integer.parseInt(line.getAttribute("time"));

                MyLine tempLine = new MyLine(lineName); // create Line

                NodeList streets_names = line.getElementsByTagName("Streets").item(0).getChildNodes();
                for (int i = 0; i < streets_names.getLength(); i++) {
                    if (streets_names.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element tmp_street = (Element) streets_names.item(i);
                        String name = tmp_street.getAttribute("name");
                        String type = tmp_street.getAttribute("type");

                        String addStreetFlag = name;

                        for (Street need_this_street : list_streets) {
                            if (name.equals(need_this_street.getId())) {
                                tempLine.addStreet(need_this_street); // add street in Line
                                tempLine.addStreetType(need_this_street.getId(),type); // add street type
                                addStreetFlag = null;
                                break;
                            }
                        }

                        if (addStreetFlag != null) {
                            System.out.println("Street with this name does not exist: " + addStreetFlag);
                            return null;
                        }
                    }
                }

                NodeList stops_names = line.getElementsByTagName("Stops").item(0).getChildNodes();
                for (int i = 0; i < stops_names.getLength(); i++) {
                    if (stops_names.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element tmp_stop = (Element) stops_names.item(i);
                        String name = tmp_stop.getAttribute("name");

                        boolean addStopFlag = false;

                        for (Stop need_this_stop : list_stops) {
                            if (name.equals(need_this_stop.getId())) {
                                tempLine.addStop(need_this_stop);
                                addStopFlag = true;
                                break;
                            }
                        }

                        if (!addStopFlag) {
                            System.out.println("Stop with this name does not exist!");
                            return null;
                        }
                    }
                }

                for(Stop stop:tempLine.getStops()){
                    tempLine.addStopsTimes(stop.getId(), 0, 0);
                    tempLine.addStopsFlags(stop.getId(), 0);
                }

                Bus tempBus = new Bus(lineName, tempLine, busColor, time_for_ring); // create new bus, name is same as line name
                tempBus.setInfo(this);
                this.list_buses.add(tempBus);

            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return this.list_buses;
    }

    /**
     * Speed up time in application
     */
    @FXML
    private void makeFaster(){
        if (Main.clock.getSpeed() > 100){
            int new_speed = Main.clock.getSpeed() - 100;
            Main.clock.setSpeed(new_speed);
            int tmp = Integer.parseInt(timeSpeedField.getText()) + 1;
            timeSpeedField.setText(String.valueOf(tmp));
        }
    }

    /**
     * Slow down time in application
     */
    @FXML
    private  void makeSlower(){
        if (Main.clock.getSpeed() < 1900){
            int new_speed = Main.clock.getSpeed() + 100;
            Main.clock.setSpeed(new_speed);
            int tmp = Integer.parseInt(timeSpeedField.getText()) - 1;
            timeSpeedField.setText(String.valueOf(tmp));
        }
    }

    /**
     * Getting base of map
     * @return Pane which represents map
     */
    public Pane getContent() {
        return  content;
    }

    /**
     * Creating of basic side menu about map
     */
    public void setTreeInfo() {
        TreeItem<String> root =  new TreeItem<>("INFO");
        TreeItem<String> streets = new TreeItem<>("Streets");
        TreeItem<String> stops = new TreeItem<>("Stops");
        TreeItem<String> buses = new TreeItem<>("Buses");

        for (Street street: list_streets){
            TreeItem<String> street_item = new TreeItem<>(street.getId());
            for (Stop stop: street.getStops()){
                TreeItem<String> tmp = new TreeItem<>(stop.getId());
                street_item.getChildren().add(tmp);
                stops.getChildren().add(tmp);
            }
            streets.getChildren().add(street_item);
        }

        for(Bus bus: list_buses){
            TreeItem<String> tmp = new TreeItem<>(bus.getBusName());
            buses.getChildren().add(tmp);
        }

        root.getChildren().add(streets);
        root.getChildren().add(stops);
        root.getChildren().add(buses);

        info.setRoot(root);
        info.setPrefWidth(infoContant.getPrefWidth());
        info.setPrefHeight(infoContant.getPrefHeight());
//        mainInfo.getChildren().add(info);
        mainInfo.toFront();
    }

    /**
     * Getting container for side menu
     * @return AnchorPane where side menu is putted
     */
    public AnchorPane getInfoContant() {
        return infoContant;
    }

    /**
     * Start running of bus lines (creating necessary threads), starting clock
     */
    @FXML
    private void startRun() {
            String newTime = clockField.getText();
            if (newTime.equals("")){
                clock.setTime(0,0,0);
            }
            else {
                String[] tmpLst= newTime.split(":");
                clock.setTime(Integer.parseInt(tmpLst[0]), Integer.parseInt(tmpLst[1]), Integer.parseInt(tmpLst[2]));
                System.out.println(Arrays.toString(tmpLst));
            }


        ExecutorService executorService = Executors.newFixedThreadPool(list_buses.size() + 2);
            for (Bus actual_bus:list_buses) {
                actual_bus.calculatePosition(clock.getTime());
                executorService.submit(new BackEnd(actual_bus));
            }
            executorService.submit(clock);
            executorService.submit(new Updater(list_buses));
            clockField.setEditable(false);
            startButton.setDisable(true);
    }

    /**
     * Setting of clock in clock field
     * @param clock_ Clock of application
     */
    public void setClock(Clock clock_) {
        clock = clock_;
    }

    /**
     * Function for showing main side menu
     */
    public void showMainMenu() {
        mainInfo.toFront();
    }

    /**
     * Function for showing main side menu after bus menu and roll back color
     * of each street in bus line.
     * @param bus Bus for which streets would be colored
     */
    public void showMainMenu(Bus bus) {
        mainInfo.toFront();
        for (Street street : bus.getBusLine().getStreets()){
            street.rollBackLineColor(bus.getColor());
        }
        lineClicked = null;
    }

    /**
     * Convert given given number to time format HH:MM:SS
     *
     * @param i Number of seconds
     * @return String representation of time
     */
    private String secToTime(int i) {
        int hours = i / 3600;
        int minutes = (i % 3600) / 60;
        int seconds = i % 60;

        return hours + ":" + minutes + ":" + seconds;
    }

    /**
     * Method dynamically creates side menu for given stop.
     * List of buses need to show time left to bus arriving
     *
     * @param id Name of stop side menu would be shown for
     * @param list List of buses of given stop
     */
    public void showStopMenu(String id, List<Bus> list) {
        stopNameField.setText(id);
        bussesBox.getChildren().clear();
        for (Bus bus : list){
            Text text = new Text(bus.getBusName() + " -> ");
            VBox time = new VBox(1);
            Text tmp = new Text(secToTime(bus.getBusLine().getStopsTimes().get(id)));
            Text nextTmp = new Text(secToTime(bus.getBusLine().getStopsTimes().get(id) + bus.getTimeForRing()));
            Text nextTmp1 = new Text(secToTime(bus.getBusLine().getStopsTimes().get(id) + bus.getTimeForRing() * 2));
            Text nextTmp2 = new Text(secToTime(bus.getBusLine().getStopsTimes().get(id) + bus.getTimeForRing() * 3));
            time.getChildren().addAll(tmp, nextTmp, nextTmp1, nextTmp2);
            bussesBox.getChildren().add(new HBox(2, text, time));
        }
        if (lineClicked != null){
            for (Street street : lineClicked.getBusLine().getStreets()){
                street.rollBackLineColor(lineClicked.getColor());
            }
            lineClicked = null;
        }
        stopMenu.setVisible(true);
        stopMenu.toFront();
    }

    /**
     * Method for showing side menu for bus and painting corresponding bus line.
     * If any other bus lien is selected, then this selection would be canceled
     * and streets corresponding to this selection would be recolored to previous
     * color
     *
     * @param bus Bus for which side menu would be shown
     */
    public void showBusMenu(Bus bus) {
        for (Street street : bus.getBusLine().getStreets()){
            street.changeLineColor(bus.getColor());
        }
        deselectObjects();
        lineClicked = bus;
        busNameField.setText(bus.getBusName());
        getBusTreeView().setRoot(bus.getRoot());
        busMenu.toFront();
    }

    /**
     * If any line is selected, then it would be deselected to show another object
     */
    public void deselectObjects(){
        if (lineClicked != null){
            for (Street street : lineClicked.getBusLine().getStreets()){
                street.rollBackLineColor(lineClicked.getColor());
            }
        }
    }

    /**
     * Show given side menu of clicked street
     * @param infoPane Side menu
     */
    public void showStreetMenu(AnchorPane infoPane) {
        infoPane.toFront();
    }

    private List<Double> lst = new ArrayList<>();
    public void printCord(MouseEvent mouseEvent) {
        System.out.println("Event coord - " + mouseEvent.getX() + ":" + mouseEvent.getY());
        if (mouseEvent.getButton() == MouseButton.SECONDARY){
            if (lst.size() + 2 == 4){
                content.getChildren().add(new Line(lst.get(0), lst.get(1), mouseEvent.getX(), mouseEvent.getY()));
                lst.clear();
            }
            else {
                lst.add(mouseEvent.getX());
                lst.add(mouseEvent.getY());
            }
        }
    }
}
