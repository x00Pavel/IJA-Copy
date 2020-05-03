package ija.sample;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import ija.Main;
import ija.functional.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class MainController{

    private List<Drawable> elements = new ArrayList<>();

    private List<Stop> list_stops = new ArrayList<>();
    private List<Street> list_streets = new ArrayList<>();
    private List<Bus> list_buses = new ArrayList<>();

    @FXML
    private Pane content;

    @FXML
    private Text clockField;

    @FXML
    private TextField timeSpeedField;

    @FXML
    private TextField scaleField;

    @FXML
    private AnchorPane mapParent;

    @FXML
    private AnchorPane infoContant;

    public Text getClockObj(){
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
    public List<Drawable> buildMap(File file) throws IOException {
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
//                    System.out.println(stops.getLength());
                    for (int i = 0; i < stops.getLength(); i++) {
                        if (stops.item(i).getNodeType() == Node.ELEMENT_NODE) {
                            Element tmp = (Element) stops.item(i);
                            String stopName = tmp.getAttribute("name");
//                            System.out.println(tmp.getNodeName());
                            NodeList tmp_node = tmp.getChildNodes();
                            for (int j = 0; j < tmp_node.getLength(); j++) {
                                if (tmp_node.item(j).getNodeType() == Node.ELEMENT_NODE) {
//                                    System.out.println(tmp_node.item(j).getNodeName());
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
            scaleField.setText(String.valueOf(Integer.parseInt(scaleField.getText().replace("%","")) + 10)+"%");
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
            scaleField.setText(String.valueOf(Integer.parseInt(scaleField.getText().replace("%","")) - 10)+"%");
        }
    }

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
                Integer time_for_ring = Integer.parseInt(line.getAttribute("time"));

                ija.functional.Line tempLine = ija.functional.Line.defaultLine(lineName); // create Line

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
//                                tempLine.addStop(new Stop(need_this_stop)); // add stop in Line
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
                    tempLine.addStopsTimes(stop.getId(),0);
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
        info = new TreeView<>(root);
        root.setExpanded(true);
        info.setPrefWidth(infoContant.getPrefWidth());
        info.setPrefHeight(infoContant.getPrefHeight());
        infoContant.getChildren().add(info);
    }

    public AnchorPane getInfoContant() {
        return infoContant;
    }
}
