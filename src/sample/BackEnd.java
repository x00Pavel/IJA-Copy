/*
    Author: Pavel Yadlouski (xyadlo00)
        Oleksii Korniienko (xkorni02)

    File: src/sample/BackEnd.java
    Date: 04.2020
 */
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
