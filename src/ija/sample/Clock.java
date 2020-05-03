package ija.sample;

import javafx.scene.text.Text;

import java.util.Arrays;
import java.util.List;

public class Clock implements Runnable{

    private int speed;
    private int hours;
    private int minutes;
    private int seconds;
    private Text clock_text;

    public Clock(int new_speed, int hour, int minute, int second, Text clock_){
        clock_text = clock_;
        speed = new_speed;
        hours = hour;
        if(hours < 0 || hours > 24){
            System.out.println("[ERROR] Hours should be in interval 0->24");
            System.exit(-1);
        }
        minutes = minute;
        if(minutes < 0 || minutes > 60){
            System.out.println("[ERROR] Minutes should be in interval 0->60");
            System.exit(-1);
        }
        seconds = second;
        if(seconds < 0 || seconds > 60){
            System.out.println("[ERROR] Seconds should be in interval 0->60");
            System.exit(-1);
        }
    }

    public void setSpeed(int s){
        speed = s;
    }

    public int getSpeed(){
        return speed;
    }

    @Override
    public void run() {
        String hours_for_print;
        String minutes_for_print;
        String seconds_for_print;
        while(true){
            seconds++;
            if(seconds == 60){
                minutes++;
                seconds = 0;
            }
            if(seconds < 10){
                seconds_for_print = "0" + Integer.toString(seconds);
            }else{
                seconds_for_print = Integer.toString(seconds);
            }
            if(minutes == 60){
                hours++;
                minutes = 0;
            }
            if(minutes < 10){
                minutes_for_print = "0" + Integer.toString(minutes);
            }else{
                minutes_for_print = Integer.toString(minutes);
            }
            if(hours == 24){
                hours = 0;
            }
            if(hours < 10){
                hours_for_print = "0" + Integer.toString(hours);
            }else{
                hours_for_print = Integer.toString(hours);
            }
            clock_text.setText(hours_for_print+":"+minutes_for_print+":"+seconds_for_print);
            System.out.println(hours_for_print+":"+minutes_for_print+":"+seconds_for_print);
            System.out.println("Clock speed: "+this.speed);
            try {
                Thread.sleep(getSpeed());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public List<Integer> getTime() {
        return Arrays.asList(hours, minutes, seconds);
    }
}
