package ija.sample;

import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import ija.functional.*;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;

public class MainController {

    @FXML
    private Pane content;
    public void setElements(List<Drawable> elements) {
        this.elements = elements;
        for(Drawable item: elements){
            content.getChildren().addAll(item.getGUI());
        }
    }

    private List<Drawable> elements = new ArrayList<>();

    private List<Stop> list_stops = new ArrayList<>();
    private  List<Street> list_streets = new ArrayList<>();

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
                        System.out.println(tmp.getNodeName());
                        NodeList tmp_node = tmp.getChildNodes();
                        for (int j = 0; j < tmp_node.getLength(); j++){
                            if(tmp_node.item(j).getNodeType() == Node.ELEMENT_NODE){
                                System.out.println(tmp_node.item(j).getNodeName());
                                Element tmp_coord = (Element)tmp_node.item(j);
                                int x = Integer.parseInt(tmp_coord.getAttribute("x"));
                                int y = Integer.parseInt(tmp_coord.getAttribute("y"));
                                Stop tmp_stop = new Stop("first_stop", new Coordinate(x, y));
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

//    public List<Bus> buildLines() {
//
//    }
}
