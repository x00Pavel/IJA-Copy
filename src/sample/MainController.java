/**
 * File: ija/src/sample/MainController.java
 * 
 * Author: Pavel Yadlouski (xyadlo00)
 *         Oleksii Korniienko (xkorni02)
 * 
 * Date: 04.2020
 * 
 * Description: Implementation of main controller of application. 
 */

package src.sample;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import javafx.util.Pair;
import src.Main;
import src.functional.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.w3c.dom.*;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.fxml.FXML;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainController{

    private List<Drawable> elements = new ArrayList<>();

    private List<Stop> list_stops = new ArrayList<>();
    private List<Street> list_streets = new ArrayList<>();
    private List<Pair<ExecutorService, List<Bus>>> list_lines = new ArrayList<>();
    private Clock clock;
    private Bus lineCliked;
    private Street streetCiked;
    private String mode = "default";

    private ExecutorService executorService;

    public List<Stop> getListStops(){
        return this.list_stops;
    }

    public List<Street> getListStreets(){
        return this.list_streets;
    }

    public List<Pair<ExecutorService, List<Bus>>> getListBuses(){
        return this.list_lines;
    }

    public void changeMode(String new_mode){
        this.mode = new_mode;
    }

    public String getMode(){
        return this.mode;
    }

    @FXML
    private AnchorPane busMenu;

    @FXML
    private TreeView<String> busTreeView;

    @FXML
    private TextField busNameField;

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

    public TreeView<String> getBusTreeView() {
        return busTreeView;
    }

    public TextField getBusNameField() {
        return busNameField;
    }


    public AnchorPane getMainInfo() {
        return mainInfo;
    }


    public TextField getStopNameField() {
        return stopNameField;
    }

    public AbstractMap.SimpleImmutableEntry<Integer, Street> getSteer(String id){
        for (Street street: list_streets){
            if (street.getId().equals(id)){
                return new AbstractMap.SimpleImmutableEntry<>(list_streets.indexOf(street), street);
            }
        }
        throw new NoSuchElementException("Street not found while searching");
    }


    public TextField getClockObj(){
        return clockField;
    }

    public AnchorPane getMapParent() {
        return mapParent;
    }

    public void setElements(List<Drawable> elements) {
        this.elements = elements;
        for(Drawable item: elements){
            content.getChildren().addAll(item.getGUI());
        }
    }

    @FXML
    public List<Drawable> buildMap(File file, FXMLLoader menuController) throws IOException {
        // Make grid for testing
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

    @FXML
    private void onScroll(ScrollEvent event){
        // If this would not be set, then zooming would be propagate to parent elements
        event.consume();
        double zoom = event.getDeltaY() > 0 ? 1.1 : 0.9;
        content.setScaleX(zoom * content.getScaleX());
        content.setScaleY(zoom * content.getScaleY());
        content.layout();
    }

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
     * Create transport lines from XML file
     *
     * @param file XML file with bus lines (streets and stops that corresponds to give line)
     * @param menuLoader Loader of FXML tempalte for side menu
     * @return List of Bus object
     */
    @FXML
    public List<Pair<ExecutorService, List<Bus>>> buildLines(File file, FXMLLoader menuLoader){
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

                src.functional.Line tempLine = src.functional.Line.defaultLine(lineName); // create Line

                NodeList streets_names = line.getElementsByTagName("Streets").item(0).getChildNodes();
                for (int i = 0; i < streets_names.getLength(); i++) {
                    if (streets_names.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element tmp_street = (Element) streets_names.item(i);
                        String name = tmp_street.getAttribute("name");
                        String type = tmp_street.getAttribute("type");

                        boolean addStreetFlag = false;

                        for (Street need_this_street : list_streets) {
                            if (name.equals(need_this_street.getId())) {
                                tempLine.addStreet(need_this_street); // add street in Line
                                tempLine.addStreetType(need_this_street.getId(),type); // add street type
                                addStreetFlag = true;
                                break;
                            }
                        }

                        if (!addStreetFlag) {
                            System.out.println("Street with this name does not exist!");
                            return null;
                        } else {
                            addStreetFlag = false;
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
                    // tempLine.createOriginalStopsTimes(stop.getId(), 0);
                    tempLine.addStopsTimes(stop.getId(), 0, 0);
                    tempLine.addStopsFlags(stop.getId(), 0);
                }
                tempLine.setInterval(Integer.parseInt(line.getAttribute("interval")));

                int numberOfBuses = Integer.parseInt(line.getAttribute("count"));
                List<Bus> buses = new ArrayList<>();
                for (int i = 0; i < numberOfBuses; i++){
                    Bus tempBus = new Bus(lineName, tempLine, busColor, time_for_ring); // create new bus, name is same as line name
                    tempBus.setInfo(this);
                    buses.add(tempBus);
                }
                this.list_lines.add(new Pair<>(Executors.newFixedThreadPool(numberOfBuses + 2), buses));

            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return this.list_lines;
    }

    /**
     * Method for speeding up time
     */
    @FXML
    private void makeFaster(){
        if (Main.clock.getSpeed() > 200){
            int new_speed = Main.clock.getSpeed() - 200;
            Main.clock.setSpeed(new_speed);
            int tmp = Integer.parseInt(timeSpeedField.getText()) + 1;
            timeSpeedField.setText(String.valueOf(tmp));
        }
    }

    /**
     * Method for slowing down time
     */
    @FXML
    private  void makeSlower(){
        if (Main.clock.getSpeed() < 1800){
            int new_speed = Main.clock.getSpeed() + 200;
            Main.clock.setSpeed(new_speed);
            int tmp = Integer.parseInt(timeSpeedField.getText()) - 1;
            timeSpeedField.setText(String.valueOf(tmp));
        }
    }

    @FXML
    private TreeView<String> info;

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

        for(Pair<ExecutorService, List<Bus>> pair: list_lines){
            for (Bus bus : pair.getValue()){
                TreeItem<String> tmp = new TreeItem<>(bus.getBusName());
                buses.getChildren().add(tmp);
            }
        }
        
        root.getChildren().add(streets);
        root.getChildren().add(stops);
        root.getChildren().add(buses);

        info.setRoot(root);
        info.setPrefWidth(mainInfo.getPrefWidth());
        info.setPrefHeight(mainInfo.getPrefHeight());
        mainInfo.toFront();
    }

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


            for(Pair<ExecutorService, List<Bus>> pair: list_lines) {
                for (int i = 0; i < pair.getValue().size(); i++) {
                    Bus bus = pair.getValue().get(i);
                    List<Integer> tmp = clock.getTime();
                    tmp.set(1, tmp.get(1) + i * bus.getBusLine().getInterval()); // Offset bus position in time - delay 5 minute
                    bus.calculatePosition(tmp);
                    pair.getKey().submit(new BackEnd(bus));
                }
                pair.getKey().submit(clock);
                pair.getKey().submit(new Updater(pair.getValue()));
            }
            clockField.setEditable(false);
            startButton.setDisable(true);
    }


    public void setClock(Clock clock_) {
        clock = clock_;
    }

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
        if (lineCliked != null){
            for (Street street : lineCliked.getBusLine().getStreets()){
                street.rollBackLineColor(lineCliked.getColor());
            }
            lineCliked = null;
        }
        stopMenu.setVisible(true);
        stopMenu.toFront();
    }

    private String secToTime(int i) {
        int hours = i / 3600;
        int minutes = (i % 3600) / 60;
        int seconds = i % 60;

        return hours + ":" + minutes + ":" + seconds;
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
        lineCliked = null;
    }

    public AnchorPane getInfoContant() {
        return infoContant;
    }

    public List<Pair<ExecutorService, List<Bus>>> getListLines() {
        return list_lines;
    }

    /**
     * Method for showing side menu for bus and painting corresponding bus line.
     * If any other bus lien is selected, then this selecetion would be canceled
     * and streets corresponding to this selection would be recolored to previos
     * color
     *
     * @param bus Bus for which side menu would be shown
     */
    public void showBusMenu(Bus bus) {
        for (Street street : bus.getBusLine().getStreets()){
            street.changeLineColor(bus.getColor());
        }
        if (lineCliked != null){
            for (Street street : lineCliked.getBusLine().getStreets()){
                street.rollBackLineColor(lineCliked.getColor());
            }
        }
        lineCliked = bus;
        busNameField.setText(bus.getBusName());
        getBusTreeView().setRoot(bus.getRoot());
        busMenu.toFront();
    }

}