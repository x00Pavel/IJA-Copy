/*
    Author: Pavel Yadlouski (xyadlo00)

    File: src/functional/Drawable.java
    Date: 04.2020
 */
package src.functional;

import src.sample.MainController;
import javafx.scene.shape.Shape;

import java.util.List;

/**
 * Interface for graphical object of application
 */
public interface Drawable {

    List<Shape> getGUI();

    /**
     * Method for putting graphical object of map
     * @param controller Main controller of scene
     */
    void setInfo(MainController controller);

}
