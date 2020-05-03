package ija.sample;

public class Clock implements Runnable{

    private int speed = 1000;
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
        String hours_for_print;
        String minutes_for_print;
        String seconds_for_print;
        while(true){
            this.seconds++;
            if(this.seconds == 60){
                this.minutes++;
                this.seconds = 0;
            }
            if(this.seconds < 10){
                seconds_for_print = "0" + Integer.toString(this.seconds);
            }else{
                seconds_for_print = Integer.toString(this.seconds);
            }
            if(this.minutes == 60){
                this.hours++;
                this.minutes = 0;
            }
            if(this.minutes < 10){
                minutes_for_print = "0" + Integer.toString(this.minutes);
            }else{
                minutes_for_print = Integer.toString(this.minutes);
            }
            if(this.hours == 24){
                this.hours = 0;
            }
            if(this.hours < 10){
                hours_for_print = "0" + Integer.toString(this.hours);
            }else{
                hours_for_print = Integer.toString(this.hours);
            }
            System.out.println(hours_for_print+":"+minutes_for_print+":"+seconds_for_print);
            try {
                Thread.sleep(this.speed);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
