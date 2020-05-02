package ija.sample;

import ija.functional.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackEnd implements Runnable{

    private Bus bus_to_run;
    private Clock clock;
    public BackEnd(Bus mybus, Clock clock){
        this.bus_to_run = mybus;
        this.clock = clock;

    }

    @Override
    public void run() {
//        bus_to_run.calculatePosition(this.hours, this.minutes, this.seconds);
        while(true) {
//            System.out.println("bus_line:   " + bus_to_run.getBusLine());
//            System.out.println("bus_line_for_use:   " + bus_to_run.getBusLineForUse());
            bus_to_run.Move();
            bus_to_run.setBusLineForUse(bus_to_run.getBusLine());
        }
    }
}
