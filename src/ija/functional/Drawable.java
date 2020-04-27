package ija.functional;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.List;

public interface Drawable {
// Extend to getID
    List<Shape> getGUI();

    void setInfo(Pane container);
//    void createInfo();
//
//    void setInfoEvent();
}
