package sample;

import javafx.scene.shape.Circle;
import static sample.BackEnd.firstBus;

public class Updater implements Runnable{

    private Circle bus;

    public Updater(Circle busOneOne){
        this.bus = busOneOne;
    }

    @Override
    public void run() {
        while(true) {
            this.bus.setCenterX(firstBus.getBusX());
            this.bus.setCenterY(firstBus.getBusY());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
