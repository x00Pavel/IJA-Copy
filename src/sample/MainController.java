package src.sample;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import src.Main;
import src.functional.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXML;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class MainController{

    private List<Drawable> elements = new ArrayList<>();

    private List<Stop> list_stops = new ArrayList<>();
    private List<Street> list_streets = new ArrayList<>();
    private List<Bus> list_buses = new ArrayList<>();
    private Clock clock;
    private String mode = "default";

    private ExecutorService executorService;

    public List<Stop> getListStops(){
        return this.list_stops;
    }

    public List<Street> getListStreets(){
        return this.list_streets;
    }

    public List<Bus> getListBuses(){
        return this.list_buses;
    }

    public void changeMode(String new_mode){
        this.mode = new_mode;
    }

    public String getMode(){
        return this.mode;
    }

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

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

    public Button getStopButton() {
        return stopButton;
    }

    public AbstractMap.SimpleImmutableEntry<Integer, Street> getSteer(String id){
        for (Street street: list_streets){
            if (street.getId().equals(id)){
                return new AbstractMap.SimpleImmutableEntry<>(list_streets.indexOf(street), street);
            }
        }
        throw new NoSuchElementException("Street not found while searching");
    }


    public Button getStartButton() {
        return startButton;
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

    @FXML
    public List<Bus> buildLines(File file, FXMLLoader menuLoader){

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
                        } else {
                            addStopFlag = false;
                        }
                    }
                }

                for(Stop stop:tempLine.getStops()){
                    // tempLine.createOriginalStopsTimes(stop.getId(), 0);
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

    @FXML
    private void makeFaster(ActionEvent event){
        if (Main.clock.getSpeed() > 200){
            int new_speed = Main.clock.getSpeed() - 200;
            Main.clock.setSpeed(new_speed);
            int tmp = Integer.parseInt(timeSpeedField.getText()) + 1;
            timeSpeedField.setText(String.valueOf(tmp));
        }
    }

    @FXML
    private  void makeSlower(ActionEvent event){
        if (Main.clock.getSpeed() < 1800){
            int new_speed = Main.clock.getSpeed() + 200;
            Main.clock.setSpeed(new_speed);
            int tmp = Integer.parseInt(timeSpeedField.getText()) - 1;
            timeSpeedField.setText(String.valueOf(tmp));
        }
    }

    public Pane getContent() {
        return  content;
    }

    private TreeView<String> info;

    public TreeView<String> getInfo() {
        return info;
    }


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
        
        root.getChildren().addAll(streets, stops, buses);
        root.setExpanded(true);
        info = new TreeView<>(root);
        AnchorPane.setLeftAnchor(info, 0.0);
        AnchorPane.setRightAnchor(info, 0.0);
        AnchorPane.setTopAnchor(info, 0.0);
        AnchorPane.setBottomAnchor(info, 0.0);
        info.setPrefWidth(infoContant.getPrefWidth());
        info.setPrefHeight(infoContant.getPrefHeight());
        infoContant.getChildren().add(info);
    }

    public AnchorPane getInfoContant() {
        return infoContant;
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


            executorService = Executors.newFixedThreadPool(list_buses.size()+2);
            for (Bus actual_bus:list_buses) {
                actual_bus.calculatePosition(clock.getTime());
                executorService.submit(new BackEnd(actual_bus));
            }
            executorService.submit(clock);
            executorService.submit(new Updater(list_buses));
            clockField.setEditable(false);
            startButton.setDisable(true);
            stopButton.setDisable(false);
    }

    // public List<Thread> getBusesThread(){
    //     return Arrays.asList(this.bus_1, this.bus_2, this.bus_3, this.bus_4);
    // }

    @FXML
    private void stopRun() throws InterruptedException {
//        startButton.setDisable(false);
//        stopButton.setDisable(true);
//        if (executorService.awaitTermination(10, TimeUnit.SECONDS)) {
//            System.out.println("task completed");
//        } else {
//            System.out.println("Forcing shutdown...");
//            executorService.shutdownNow();
//        }
//        executorService.shutdownNow();

    }

    public void setClock(Clock clock_) {
        clock = clock_;
    }
}
