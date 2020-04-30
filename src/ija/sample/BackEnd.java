package ija.sample;

import ija.functional.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackEnd implements Runnable{

    private Bus bus_to_run;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    public BackEnd(Bus mybus, Integer hours, Integer minutes, Integer seconds){
        this.bus_to_run = mybus;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
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
