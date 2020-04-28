package ija.sample;

import ija.functional.Bus;
import javafx.application.Platform;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.List;

public class Updater implements Runnable{

    private List<Bus> all_buses;

    public Updater(List<Bus> mybus){
        this.all_buses = mybus;
    }

    @Override
    public void run() {
        while(true) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    for(Bus bus: Updater.this.all_buses){
                        Circle c = (Circle)bus.getGUI().get(0);
                        c.setCenterX(bus.getBusX());
                        c.setCenterY(bus.getBusY());
                    }
                }
            });
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
