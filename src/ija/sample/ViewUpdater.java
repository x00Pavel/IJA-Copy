package ija.sample;

import ija.functional.Drawable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class ViewUpdater implements Runnable{

    Stage primaryStage;
    Scene scene;
    MainController controller;
    List<Drawable> items;

    public ViewUpdater(Stage primaryStage, Scene scene, MainController controller, List<Drawable> items){
        this.primaryStage = primaryStage;
        this.scene = scene;
        this.controller = controller;
        this.items = items;
    }

    @Override
    public void run() {
//        while(true) {
//            this.primaryStage.close();
            this.controller.setElements(this.items);
            this.primaryStage.setScene(this.scene);
            this.primaryStage.show();
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
