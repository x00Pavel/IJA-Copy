package  ija;

import ija.functional.Bus;
import ija.functional.Drawable;

import ija.sample.BackEnd;
import ija.sample.Updater;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import javafx.stage.Stage;
import ija.sample.MainController;

import java.io.File;
import java.io.IOException;
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

        try {
            items = controller.buildMap(fileMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        list_bus = controller.buildLines(fileTransport);
        items.addAll(list_bus);


        controller.setElements(items);
        primaryStage.setScene(scene);
        primaryStage.show();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ExecutorService executorService = Executors.newFixedThreadPool(list_bus.size()+1); // change 4->5 for ViewUpdater ON
        for (Bus actual_bus:list_bus) {
            executorService.submit(new BackEnd(actual_bus));
        }
        executorService.submit(new Updater(list_bus));

        primaryStage.setOnCloseRequest(event -> {
            System.exit(0);
        });

    }
}
