/*
  File: ija/src/Main.java

  Author: Pavel Yadlouski (xyadlo00)
          Oleksii Korniienko (xkorni02)

  Date: 04.2020

  Description: Main class of application. Sets main scene and run application
 */


package src;

import javafx.scene.control.TextField;
import javafx.util.Pair;
import src.functional.Bus;
import src.functional.Drawable;

import src.sample.MainController;
import src.sample.Clock;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class Main extends Application {

    public static List<Pair<ExecutorService, List<Bus>>> list_lines;
    public static List<Drawable> items;
    public static MainController controller;
    public static Clock clock;

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
            items = controller.buildMap(fileMap, sideLoader);
            list_lines = controller.buildLines(fileTransport, sideLoader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Pair<ExecutorService, List<Bus>> pair : list_lines){
            items.addAll(pair.getValue());
        }

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

    public static void main(String[] args) {
        launch(args);
    }
}
