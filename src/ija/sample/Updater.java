package ija.sample;

import ija.functional.Bus;
import javafx.application.Platform;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.List;

public class Updater implements Runnable{

    private List<Bus> bus_to_run;

    public Updater(List<Bus> mybus){
        this.bus_to_run = mybus;
    }

    @Override
    public void run() {

        Circle bus_0 = (Circle)bus_to_run.get(0).getGUI().get(0);
        Circle bus_1 = (Circle)bus_to_run.get(1).getGUI().get(0);
        Circle bus_2 = (Circle)bus_to_run.get(2).getGUI().get(0);
        while(true) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    bus_0.setCenterX(bus_to_run.get(0).getBusX());
                    bus_0.setCenterY(bus_to_run.get(0).getBusY());

                    bus_1.setCenterX(bus_to_run.get(1).getBusX());
                    bus_1.setCenterY(bus_to_run.get(1).getBusY());

                    bus_2.setCenterX(bus_to_run.get(2).getBusX());
                    bus_2.setCenterY(bus_to_run.get(2).getBusY());
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
