/**
 * File: ija/src/functional/Drawable.java
 * 
 * Author: Pavel Yadlouski (xyadlo00)
 *         Oleksii Korniienko (xkorni02)
 * 
 * Date: 04.2020
 * 
 * Description: Interface for objects that would be on the scene 
 */


package src.functional;

import src.sample.MainController;
import javafx.scene.shape.Shape;

import java.util.List;

public interface Drawable {

    /**
     * Take graphical representation of object
     */
    List<Shape> getGUI();

    /**
     * Create side menu for corresponding object
     * 
     * @param container Main controller of scene
     */
    void setInfo(MainController container);

}
