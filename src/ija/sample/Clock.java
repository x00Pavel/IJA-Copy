package ija.sample;

import ija.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Clock implements Runnable{

    private int speed = 1;
    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;


    public Clock(){

    }

    public Clock(int new_speed, int hours, int minutes, int seconds){
        this.speed = new_speed;
        this.hours = hours;
        if(hours < 0 || hours > 24){
            System.out.println("[ERROR] Hours should be in interval 0->24");
            System.exit(-1);
        }
        this.minutes = minutes;
        if(minutes < 0 || minutes > 60){
            System.out.println("[ERROR] Minutes should be in interval 0->60");
            System.exit(-1);
        }
        this.seconds = seconds;
        if(seconds < 0 || seconds > 60){
            System.out.println("[ERROR] Seconds should be in interval 0->60");
            System.exit(-1);
        }
    }

    @Override
    public void run() {
        while(true){
            this.seconds++;
            if(this.seconds == 60){
                this.minutes++;
                this.seconds = 0;
            }
            if(this.minutes == 60){
                this.hours++;
                this.minutes = 0;
            }
            if(this.hours == 24){
                this.hours = 0;
            }
            System.out.println(this.hours+":"+this.minutes+":"+this.seconds);
            try {
                Thread.sleep(Main.getClockSpeed());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
