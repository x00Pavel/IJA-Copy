package ija.sample;

import ija.functional.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackEnd implements Runnable{

    private Bus bus_to_run;

    public BackEnd(Bus mybus){
        this.bus_to_run = mybus;
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(1); // thread for show road of bus
        executorService.submit(new ShowRoad(this.bus_to_run, this.bus_to_run.getColor()));
        while(true) {
            bus_to_run.Move();
        }
    }
}
