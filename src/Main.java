package src;

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
        System.out.println("FUCK HERE##################################");
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("layout.fxml")));
        File fileMap = new File(Objects.requireNonNull("data/map.xml"));
        File fileTransport = new File(Objects.requireNonNull("data/transport.xml"));

        BorderPane root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);
        controller = loader.getController();

        clock = new Clock(1000,0,0,0, controller.getClockObj());
        try {
            FXMLLoader sideLoader = new FXMLLoader(getClass().getClassLoader().getResource("sideMenu.fxml"));
            items = controller.buildMap(fileMap, sideLoader);
            list_bus = controller.buildLines(fileTransport, sideLoader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        items.addAll(list_bus);

        for(Bus bus: list_bus){ // for every bus calculate a start position
            bus.calculatePosition(clock.getTime());
        }
        controller.setElements(items);
        controller.setTreeInfo();
        primaryStage.setTitle("Transport app");
        primaryStage.setMinHeight(733);
        primaryStage.setMinWidth(970);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ExecutorService executorService = Executors.newFixedThreadPool(list_bus.size()+2);
        for (Bus actual_bus:list_bus) {
            executorService.submit(new BackEnd(actual_bus, clock));
        }

        executorService.submit(clock);
        executorService.submit(new Updater(list_bus));

        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
