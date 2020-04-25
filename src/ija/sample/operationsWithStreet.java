package ija.sample;

import ija.functional.Drawable;
import ija.functional.Street;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;

import java.util.List;

import static ija.Main.items;

public class operationsWithStreet implements Runnable{

    Street street;
    String operation;
    String color;

    public operationsWithStreet(Street street, String operation, String color){
        this.street = street;
        this.operation = operation;
        this.color = color;
    }

    @Override
    public void run() {
//        Line line = new Line(this.street.begin().getX(), this.street.begin().getY(), this.street.end().getX(), this.street.end().getY());
//        line.setStroke(Color.RED);
//        items.add((Drawable) line);
        if (this.operation.equals("paint")) {
            List<Shape> elements = this.street.getGUI();
            for (Shape element : elements) {
                element.setStroke(Color.web(this.color, 1.0));
            }
//            Polyline line = new Polyline(); //here must be code for paint street
//            line.setStroke(Color.RED);
//            Line line = new Line(this.street.begin().getX(), this.street.begin().getY(), this.street.end().getX(), this.street.end().getY());
//            line.setStroke(Color.RED);
        }else{
            System.out.println("Unknown operation!");
        }
    }
}
