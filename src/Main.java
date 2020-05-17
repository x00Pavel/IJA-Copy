/*
    Author: Pavel Yadlouski (xyadlo00)
            Oleksii Korniienko (xkorni02)

    File: src/Main.java
    Date: 04.2020
 */

package src;

import src.functional.Bus;
import src.functional.Drawable;
import src.sample.MainController;
import src.sample.Clock;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Main class. Here is loading of necessary files is done
 */
public class Main extends Application {

    public static List<Bus> list_bus;
    public static List<Drawable> items;
    public static MainController controller;
    public static Clock clock;

    /**
     * Starting of main window
     *
     * @param primaryStage Main stage to be shown
     */
    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("layout.fxml")));
        File fileMap = new File("data/map.xml");
        File fileTransport = new File("data/transport.xml");

        BorderPane root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert root != null;
        Scene scene = new Scene(root);
        controller = loader.getController();
        TextField clockField = controller.getClockObj();
        clock = new Clock(1000, clockField);
        clock.setTime(0,1,21);
        try {
            FXMLLoader sideLoader = new FXMLLoader(getClass().getClassLoader().getResource("sideMenu.fxml"));
            items = controller.buildMap(fileMap);
            list_bus = controller.buildLines(fileTransport);
        } catch (IOException e) {
            e.printStackTrace();
        }
        items.addAll(list_bus);

        controller.setClock(clock);
        controller.setElements(items);
        controller.setTreeInfo();
        primaryStage.setTitle("Transport app");
        primaryStage.setMinHeight(733);
        primaryStage.setMinWidth(970);
        primaryStage.setScene(scene);
        primaryStage.show();


        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }

    /**
     * Invocation method
     * @param args Arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
