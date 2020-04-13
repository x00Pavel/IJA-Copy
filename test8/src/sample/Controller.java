package sample;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Circle busOne;

    @FXML
    private Button busMoveDown;

    @FXML
    void initialize() {
        busMoveDown.setOnAction(event -> {
           busOne.setCenterY(busOne.getCenterY()+5);
        });
    }
}
