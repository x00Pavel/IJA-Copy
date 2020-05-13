package src.sample;

import src.functional.Bus;

public class BackEnd implements Runnable{

    private final Bus bus_to_run;

    public BackEnd(Bus myBus){
        this.bus_to_run = myBus;
    }

    @Override
    public void run() {
        while(!Thread.interrupted()) {
            bus_to_run.Move();
        }
    }
}
