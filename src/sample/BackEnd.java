package src.sample;

import src.functional.Bus;

public class BackEnd implements Runnable{

    private Bus bus_to_run;
    // private Clock clock;
    public BackEnd(Bus mybus){
        this.bus_to_run = mybus;
        // this.clock = clock;

    }

    @Override
    public void run() {
        while(true) {
            bus_to_run.Move();
            // bus_to_run.setBusLineForUse(bus_to_run.getBusLine());
        }
    }
}
