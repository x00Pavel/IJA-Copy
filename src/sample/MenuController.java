/*
    Author: Pavel Yadlouski (xyadlo00)
            Oleksii Korniienko (xkorni02)

    File: src/sample/MenuController.java
    Date: 04.2020
 */

package src.sample;

import src.functional.Street;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

/**
 * Controller of side menu for street.
 */
public class MenuController { //TODO we need a comments here

    private Street street;

    @FXML
    private TextField streetNameField;

    @FXML
    private AnchorPane sideMenu;

    @FXML
    private TreeView<String> info;

    @FXML
    private TextField streetLoading;

    @FXML
    private Button streetLoadingMinus;

    @FXML
    private Button streetLoadingPlus;

    @FXML
    private CheckBox streetBlock;

    /**
     * Get street side menu corresponds to
     * @return Street object
     */
    public Street getStreet() {
        return street;
    }

    /**
     * Set Street object for instance of controller
     * @param street Street to be added
     */
    public void setStreet(Street street) {
        this.street = street;
    }

    /**
     * Set text to street name field
     * @param name String to set in field
     */
    public void setStreetNameField(String name) {
        this.streetNameField.setText(name);
    }

    /**
     * Set selection of check box for blocking street
     * @param streetBlock Flag for selecting bix
     */
    public void setStreetBlock(Boolean streetBlock) {
        this.streetBlock.setSelected(streetBlock);
    }

    /**
     * Get check box for blocking street
     * @return Check box
     */
    public CheckBox getStreetBlock() {
        return streetBlock;
    }

    /**
     * Get tree view of elements in street menu
     * @return Tree View
     */
    public TreeView<String> getInfo() {
        return info;
    }

    /**
     * Get field with street loading
     * @return Text field
     */
    public TextField getStreetLoading() {
        return streetLoading;
    }

    /**
     * Update value of loading in text field
     * @param streetLoading New value of loading
     */
    public void setStreetLoading(String streetLoading) {
        this.streetLoading.setText(streetLoading);
    }

    /**
     * Get button to unload street
     * @return Button
     */
    public Button getStreetLoadingMinus() {
        return streetLoadingMinus;
    }

    /**
     * Get button to load street
     * @return Button
     */
    public Button getStreetLoadingPlus() {
        return streetLoadingPlus;
    }

}
