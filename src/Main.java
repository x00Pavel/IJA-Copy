package src;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import src.functional.Bus;
import src.functional.Drawable;

import src.sample.MainController;
import src.sample.BackEnd;
import src.sample.Updater;
import src.sample.Clock;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main extends Application {

    public static List<Bus> list_bus;
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
            list_bus = controller.buildLines(fileTransport, sideLoader);
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

    public static void main(String[] args) {
        launch(args);
    }
}
