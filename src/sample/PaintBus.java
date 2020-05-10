/**
 * File: ija/src/sample/PaintBus.java
 * 
 * Author: Pavel Yadlouski (xyadlo00)
 *         Oleksii Korniienko (xkorni02)
 * 
 * Date: 04.2020
 * 
 * Description: ЛЕША НАПИШИ ТУТ ПРОФ ФАЙЛ
 */


package src.sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PaintBus implements Runnable{

    Circle bus_circle;

    public PaintBus(Circle bus_circle){
        this.bus_circle = bus_circle;
    }

    @Override
    public void run() {
        this.bus_circle.setStroke(Color.BLACK);
    }
}
