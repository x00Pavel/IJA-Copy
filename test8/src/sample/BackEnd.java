package sample;

import Functional.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BackEnd {
    public static Bus firstBus;

    public void Back() throws IOException, InterruptedException {

//        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        Coordinate c01 = Coordinate.create(20, 10);
        Coordinate c02 = Coordinate.create(90, 20);
        Coordinate c03 = Coordinate.create(90, 90);
        Street s01 = Street.defaultStreet("first", c01, c02, c03);

        Coordinate c1 = Coordinate.create(728, 14);
        Coordinate c2 = Coordinate.create(728, 538);
        Coordinate c3 = Coordinate.create(473, 538);
        Street s1 = Street.defaultStreet("first", c1, c2, c3);

        Coordinate c4 = Coordinate.create(473, 83);
        Coordinate c5 = Coordinate.create(728, 83);
        Street s2 = Street.defaultStreet("second", c3, c4, c5);


        Coordinate c6 = Coordinate.create(728, 132);
        Stop stop1 = Stop.defaultStop("stop1", c6);

        Coordinate c7 = Coordinate.create(728, 306);
        Stop stop2 = Stop.defaultStop("stop2", c7);

        Coordinate c8 = Coordinate.create(633, 538);
        Stop stop3 = Stop.defaultStop("stop3", c8);

        Coordinate c9 = Coordinate.create(473, 259);
        Stop stop4 = Stop.defaultStop("stop4", c9);

        Coordinate c10 = Coordinate.create(608, 83);
        Stop stop5 = Stop.defaultStop("stop5", c10);

        s1.addStop(stop1);
        s1.addStop(stop2);
        s1.addStop(stop3);

        s2.addStop(stop4);
        s2.addStop(stop5);

        Line line1 = Line.defaultLine("10");

        line1.addStreet(s1);
        line1.addStreet(s2);

        line1.addStop(stop1);
        line1.addStop(stop2);
        line1.addStop(stop3);
        line1.addStop(stop4);
        line1.addStop(stop5);

        firstBus = new Bus("firstBus", line1);

        Thread.sleep(3000);
        firstBus.Move();
//        Circle mycircle = (Circle)root.lookup("#bus1");

//        mycircle.setCenterY(mycircle.getCenterY()+100);
//        for(int i = 0; i < 5; i++){
//            double tempY = 75;
//            double tempX = 0;
//
//            Thread.sleep(3000);
//
//            firstBus.setBusX(firstBus.getBusX()+tempX);
//            firstBus.setBusY(firstBus.getBusY()+tempY);
//        }

//        firstBus.MoveDown(mycircle);
    }
}
