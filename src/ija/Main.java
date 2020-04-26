package  ija;

import ija.functional.Bus;
import ija.functional.Drawable;

import ija.sample.BackEnd;
import ija.sample.Updater;
import ija.sample.ViewUpdater;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import javafx.stage.Stage;
import ija.sample.MainController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main extends Application {

    public static List<Bus> list_bus;
    public static List<Drawable> items;

    @Override
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));

        File fileMap = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("map.xml")).getFile());
        File fileTransport = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("transport.xml")).getFile());

        BorderPane root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene scene = new Scene(root);

        MainController controller = loader.getController();
//        ExecutorService executorService = Executors.newFixedThreadPool(1);
//        executorService.submit(new FrontEnd());

        try {
            items = controller.buildMap(fileMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list_bus = controller.buildLines(fileTransport);
        items.addAll(list_bus);

        Thread b1 = new Thread(new BackEnd(list_bus.get(0)), "b1");
        Thread b2 = new Thread(new BackEnd(list_bus.get(1)), "b2");
        Thread b3 = new Thread(new BackEnd(list_bus.get(2)), "b3");
        Thread updater = new Thread(new Updater(list_bus), "updater");
//        Thread creator = new Thread(new ViewUpdater(primaryStage, scene, controller, items), "creator");

//        executorService.submit(new ViewUpdater(primaryStage, scene, controller, items));

        controller.setElements(items);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ExecutorService executorService = Executors.newFixedThreadPool(4); // change 4->5 for ViewUpdater ON
        executorService.submit(b1);
        executorService.submit(b2);
        executorService.submit(b3);
//        executorService.submit(creator);
//        controller.runUpdater();
        executorService.submit(updater);


//        list_bus.get(0).Move();

    }
}
