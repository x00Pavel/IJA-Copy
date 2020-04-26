package ija.sample;

import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import ija.functional.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class MainController{

    private List<Drawable> elements = new ArrayList<>();

    private List<Stop> list_stops = new ArrayList<>();
    private List<Street> list_streets = new ArrayList<>();

    private List<Bus> allBuses = new ArrayList<>();

//    private Circle bus_0;
//    private Circle bus_1;
//    private Circle bus_2;

    @FXML
    private Pane content;
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

                NodeList stops = street.getElementsByTagName("Stops").item(0).getChildNodes();
                System.out.println(stops.getLength());
                for (int i = 0; i < stops.getLength(); i++){
                    if(stops.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element tmp = (Element)stops.item(i);
                        String stopName = tmp.getAttribute("name");
                        System.out.println(tmp.getNodeName());
                        NodeList tmp_node = tmp.getChildNodes();
                        for (int j = 0; j < tmp_node.getLength(); j++){
                            if(tmp_node.item(j).getNodeType() == Node.ELEMENT_NODE){
                                System.out.println(tmp_node.item(j).getNodeName());
                                Element tmp_coord = (Element)tmp_node.item(j);
                                int x = Integer.parseInt(tmp_coord.getAttribute("x"));
                                int y = Integer.parseInt(tmp_coord.getAttribute("y"));
                                Stop tmp_stop = new Stop(stopName, new Coordinate(x, y));
                                street_stops.add(tmp_stop);
                                list_stops.add(tmp_stop);
                            }
                        }
                    }
                }
                Street new_street = Street.defaultStreet(street.getAttribute("name"), street_cords, street_stops);
                list_streets.add(new_street);
                elements.add(new_street);

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
        event.consume();
        double zoom =1.1;
        content.setScaleX(zoom * content.getScaleX());
        content.setScaleY(zoom * content.getScaleY());
        content.layout();
    }

    @FXML
    private void makeSmaller(ActionEvent event){
        event.consume();
        double zoom = 0.9;
        content.setScaleX(zoom * content.getScaleX());
        content.setScaleY(zoom * content.getScaleY());
        content.layout();
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

                ija.functional.Line tempLine = ija.functional.Line.defaultLine(lineName); // create Line

                NodeList streets_names = line.getElementsByTagName("Streets").item(0).getChildNodes();
                for (int i = 0; i < streets_names.getLength(); i++) {
                    if (streets_names.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        Element tmp_street = (Element) streets_names.item(i);
                        String name = tmp_street.getAttribute("name");

                        boolean addStreetFlag = false;

                        for (Street need_this_street : list_streets) {
                            if (name.equals(need_this_street.getId())) {
                                tempLine.addStreet(need_this_street); // add street in Line
                                addStreetFlag = true;
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
                                tempLine.addStop(need_this_stop); // add stop in Line
                                addStopFlag = true;
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


                Bus tempBus = new Bus(lineName, tempLine, busColor); // create new bus, name is same as line name
                this.allBuses.add(tempBus);

            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return this.allBuses;
    }

//    private Service<Void> backgroundThread_0;
//    private Service<Void> backgroundThread_1;
//    private Service<Void> backgroundThread_2;

//    public void runUpdater(){
//
//        bus_0 = (Circle)this.allBuses.get(0).getGUI().get(0);
//        bus_1 = (Circle)this.allBuses.get(1).getGUI().get(0);
////        bus_2 = (Circle)this.allBuses.get(2).getGUI().get(0);
//
//        Bus bus_0_c = this.allBuses.get(0);
//        Bus bus_1_c = this.allBuses.get(1);
////        Bus bus_2_c = this.allBuses.get(2);
//
//        backgroundThread_0 = new Service<Void>() {
//
//            @Override
//            protected Task<Void> createTask() {
//                return new Task<Void>() {
//                    @Override
//                    protected Void call() throws Exception {
//
//                        while(true) {
//                            bus_0.setCenterX(bus_0_c.getBusX());
//                            bus_0.setCenterY(bus_0_c.getBusY());
//
//                            bus_1.setCenterX(bus_1_c.getBusX());
//                            bus_1.setCenterY(bus_1_c.getBusY());
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                };
//            }
//        };
//
////        backgroundThread_1 = new Service<Void>() {
////            @Override
////            protected Task<Void> createTask() {
////                return new Task<Void>() {
////                    @Override
////                    protected Void call() throws Exception {
////                        while(true) {
////                            bus_1.setCenterX(bus_1_c.getBusX());
////                            bus_1.setCenterY(bus_1_c.getBusY());
////                            try {
////                                Thread.sleep(100);
////                            } catch (InterruptedException e) {
////                                e.printStackTrace();
////                            }
////                        }
////                    }
////                };
////            }
////        };
//
////        bus_0.centerXProperty().bind(backgroundThread.);
////        bus_0.centerYProperty().bind(backgroundThread.totalWorkProperty());
//
//        backgroundThread_0.restart();
////        backgroundThread_1.restart();
//    }
}
