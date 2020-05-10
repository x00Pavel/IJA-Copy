/*
  File: ija/src/sample/backEnd.java

  Author: Pavel Yadlouski (xyadlo00)
          Oleksii Korniienko (xkorni02)

  Date: 04.2020

  Description: ЛЕША НАПИЩИ ТУТ ОПИСАНИЕ ЭТОГО ФАЙЛА
 */

package src.sample;

import javafx.concurrent.Task;
import src.functional.Bus;

public class BackEnd extends Task<Long> {

    private final Bus bus_to_run;
    // private Clock clock;
    public BackEnd(Bus mybus){
        this.bus_to_run = mybus;
        // this.clock = clock;

    }

    @Override
    public Long call() {
        while(!Thread.interrupted()) {
            bus_to_run.Move();
            // bus_to_run.setBusLineForUse(bus_to_run.getBusLine());
        }
        return null;
    }
}
