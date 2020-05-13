package src.functional;

import src.sample.MainController;
import javafx.scene.shape.Shape;

import java.util.List;

public interface Drawable {
// Extend to getID
    List<Shape> getGUI();

    void setInfo(MainController container);

}
