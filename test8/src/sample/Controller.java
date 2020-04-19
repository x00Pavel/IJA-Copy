package sample;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;

import static sample.BackEnd.firstBus;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Circle busOneOne;

    @FXML
    private Button busMoveDown;

    @FXML
    private Button calculate;

    @FXML
    void initialize() {
        busMoveDown.setOnAction(event -> {
//            firstBus.Move(busOne);
            double tempBusOneX = busOneOne.getCenterX();
            double tempBusOneY = busOneOne.getCenterY();
            firstBus.setBusX(tempBusOneX);
            firstBus.setBusY(tempBusOneY);
//            busOneOne.setCenterY(busOneOne.getCenterY()+15);
//            firstBus.MoveDown(busOne);
        });

        calculate.setOnAction(event -> {
//            firstBus.Move(tempBusOneX, tempBusOneY);
//            while(true) {
                busOneOne.setCenterX(firstBus.getBusX());
                busOneOne.setCenterY(firstBus.getBusY());
//            }
        });
    }
}
