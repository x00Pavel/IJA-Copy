package ija.functional;

import ija.sample.MainController;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.List;

public interface Drawable {
// Extend to getID
    List<Shape> getGUI();

    void setInfo(MainController controller);
//    void createInfo();
//
//    void setInfoEvent();
}
