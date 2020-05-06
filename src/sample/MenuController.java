package src.sample;

import src.functional.Street;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

public class MenuController {

    private String streetName;

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @FXML
    AnchorPane sideMenu;

    @FXML
    TreeView<String> info;

    @FXML
    TextField streetLoading;

    @FXML
    Button streetLoadingMinus;

    @FXML
    Button streetLoadingPlus;

    @FXML
    CheckBox streetBlock;

    public CheckBox getStreetBlock() {
        return streetBlock;
    }

    public AnchorPane getSideMenu() {
        return sideMenu;
    }

    public TreeView<String> getInfo() {
        return info;
    }

    public TextField getStreetLoading() {
        return streetLoading;
    }

    public Button getStreetLoadingMinus() {
        return streetLoadingMinus;
    }

    public Button getStreetLoadingPlus() {
        return streetLoadingPlus;
    }

    @FXML
    private void loadStreet(){
        int load = Integer.parseInt(streetLoading.getText());

    }

    @FXML
    private void unloadStreet(){

    }



}
