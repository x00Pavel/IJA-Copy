package  ija;

import ija.functional.Bus;
import ija.functional.Drawable;

import ija.sample.*;
import javafx.application.Application;
import javafx.fxml.FXML;
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
    public static  Clock clock;

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("layout.fxml"));
        File fileMap = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("map.xml")).getFile());
        File fileTransport = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("transport.xml")).getFile());

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
            items = controller.buildMap(fileMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list_bus = controller.buildLines(fileTransport);
        items.addAll(list_bus);

        for(Bus bus: list_bus){ // for every bus calculate a start position
            bus.calculatePosition(clock.getTime());
        }
        controller.setElements(items);
        controller.setTreeInfo();
        primaryStage.setTitle("Transport app");
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
}
