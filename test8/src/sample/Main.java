package sample;

import Functional.Bus;
import Functional.Coordinate;
import Functional.Stop;
import Functional.Street;
import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends Application{
    private List<Bus> buses = new ArrayList<>();
    @Override
    public void start(Stage primaryStage) throws IOException, InterruptedException {

        BackEnd.createDefault();

        BorderPane border_pane = new BorderPane();
        HBox top = new HBox();
        Pane map = new Pane();
        border_pane.setTop(top);
        border_pane.setCenter(map);

        FrontEnd.DrawStreets(map);
        buses.add(new Bus("First bus", 0));
        buses.add(new Bus("Second bus", 1));

        for (Bus bus: buses){
            map.getChildren().add(bus);
            Path path = new Path();
            String line_id = bus.getLine().getId();
            BackEnd.createPath(path, line_id);
            PathTransition bus_a = FrontEnd.busMove(path, bus);
            bus_a.play();
        }



        border_pane.setCenter(map);

        Scene scene = new Scene(border_pane, 400, 400);

        primaryStage.setTitle("Transport tracking");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) throws IOException, InterruptedException {

        launch(args);
    }
}
