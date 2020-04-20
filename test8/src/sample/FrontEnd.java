package sample;

import Functional.Bus;
import Functional.Coordinate;
import Functional.Stop;
import Functional.Street;
import javafx.animation.Animation;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import sample.BackEnd;

import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Level;


public class FrontEnd{

    public static void DrawStreets(Pane map){

        // Make grid for testing
        for (int i = 0; i < 1000; i += 10){
            Line line = new Line(i, 0, i, 1000);
            line.setStroke(Color.LIGHTGRAY);
            Line hor = new Line(0, i, 1000, i);
            hor.setStroke(Color.LIGHTGRAY);
            map.getChildren().addAll(line, hor);
        }

        // Draw street lines
        for (Street new_street: BackEnd.streets) {
            List<Coordinate> coords = new_street.getCoordinates();
            List<Stop> stops = new_street.getStops();
            int size = coords.size();
            for (int i = 0; i < size; i++) {
                if (i + 1 < size) {
                    Coordinate from = coords.get(i);
                    Coordinate to = coords.get(i + 1);

                    Line new_line = new Line(from.getX(), from.getY(), to.getX(), to.getY());
                    new_line.setStroke(Color.BLACK);
                    new_line.setStrokeWidth(2);
                    map.getChildren().add(new_line);
                }
            }

            // Draw stops on lines
            for (Stop stop: stops){
                Coordinate coord = stop.getCoordinate();
                Circle cir = new Circle();
                cir.setRadius(3);
                cir.setFill(Color.ORANGE);
                cir.setCenterY(coord.getY());
                cir.setCenterX(coord.getX());
                map.getChildren().add(cir);
            }

        }

    }


    public static PathTransition busMove(Path path, Bus bus) {
        PathTransition ptr = new PathTransition();

        ptr.setDuration(Duration.seconds(2));
        ptr.setDelay(Duration.seconds(1));
        ptr.setPath(path);
        ptr.setNode(bus);
        ptr.setCycleCount(Animation.INDEFINITE);
        ptr.setAutoReverse(true);
        return ptr;

    }
}
