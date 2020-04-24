package  ija;

import ija.functional.Bus;
import ija.functional.Drawable;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

import javafx.stage.Stage;
import ija.sample.MainController;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));

        File file = new File(Objects.requireNonNull(getClass().getClassLoader().getResource("map.xml")).getFile());

        BorderPane root = loader.load();
        Scene scene = new Scene(root);

        MainController controller = loader.getController();
//        ExecutorService executorService = Executors.newFixedThreadPool(1);
//        executorService.submit(new FrontEnd());

        List<Drawable> items = controller.buildMap(file);
        Bus bus  = new Bus("bus 10 ");
//        List<Bus> list_bus = controller.buildLines();
        items.add(bus);
        //        Bus bus = controller.buildLines();
//        items.add(bus);
//        Thread.sleep(3000);
//        bus.Move();

        controller.setElements(items);
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
