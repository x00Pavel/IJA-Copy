package sample;

import Functional.*;
import Functional.Line;
import javafx.animation.PathTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

//class MyBus extends Rectangle{
//    Rectangle bus;
//    private String name;
//
//    public MyBus(String name, double x, double y){
//        this.name =  name;
//        this.bus = new Rectangle(x, y, 20, 20);
//        this.bus.setFill(Color.ORANGE);
//    }
//
//}

public class BackEnd {

//    public static Bus firstBus;
    public static List<Street> streets = new ArrayList<>();
    public static List<Functional.Line> lines = new ArrayList<>();

    private static Functional.Line getLine(String id){
        for (Functional.Line line: BackEnd.lines){
            if (id == line.getId()){
                return line;
            }
        }
        return null;
    }

    public static void createPath(Path path, String id){
        Functional.Line line = BackEnd.getLine(id);
        assert line != null;
        List<Street> streets = line.getStreets();

        for (Street street : streets) {
            List<Coordinate> coords = street.getCoordinates();
            path.getElements().add(new MoveTo(coords.get(0).getX(), coords.get(0).getY()));
            List<Stop> stops = street.getStops();
            for (int i = 1; i < coords.size(); i++) {
                Coordinate coord_1 = coords.get(i);
                path.getElements().add(new LineTo(coord_1.getX(), coord_1.getY()));
            }

        }
    }

    public static void createDefault (){
        Coordinate c1 = Coordinate.create(20, 10);
        Coordinate c2 = Coordinate.create(90, 10);
        Coordinate c3 = Coordinate.create(90, 90);
        Street s1 = Street.defaultStreet("first", c1, c2, c3);
        if (s1 == null){
            System.out.println("terminating or closing java program");
            System.exit(1); //n
        }
        Coordinate c4 = Coordinate.create(50, 130);
        Coordinate c5 = Coordinate.create(50, 90);
        Coordinate c06 = Coordinate.create(140, 90);
        Coordinate c07 = Coordinate.create(140, 130);
        Coordinate c08 = Coordinate.create(190, 130);

        Street s2 = Street.defaultStreet("second", c4, c5, c06, c07, c08);
        if (s2 == null){
            System.out.println("terminating or closing java program");
            System.exit(1); //n
        }

        Coordinate a1 = Coordinate.create(190, 130);
        Coordinate a2 = Coordinate.create(140, 170);
        Coordinate a3 = Coordinate.create(90, 170);
        Coordinate a4 = Coordinate.create(50, 200);

        Street s3 = Street.defaultStreet("A1-A2", a1, a2);
        if (s3 == null){
            System.out.println("terminating or closing java program");
            System.exit(1); //n
        }
        Street s4 = Street.defaultStreet("A2-A3", a2, a3);
        if (s4 == null){
            System.out.println("terminating or closing java program");
            System.exit(1); //n
        }
        Street s5 = Street.defaultStreet("A3-A4", a3, a4);
        if (s5 == null){
            System.out.println("terminating or closing java program");
            System.exit(1); //n
        }

        Coordinate c6 = Coordinate.create(40, 10);
        Stop stop1 = Stop.defaultStop("stop1", c6);

        Coordinate c7 = Coordinate.create(90, 40);
        Stop stop2 = Stop.defaultStop("stop1", c7);

        Coordinate c8 = Coordinate.create(90, 80);
        Stop stop3 = Stop.defaultStop("stop3", c8);

        Coordinate c9 = Coordinate.create(50, 110);
        Stop stop4 = Stop.defaultStop("stop4", c9);

        Coordinate c10 = Coordinate.create(120, 90);
        Stop stop5 = Stop.defaultStop("stop5", c10);

        Coordinate c010 = Coordinate.create(140, 120);
        Stop stop50 = Stop.defaultStop("stop5", c010);

        Coordinate c020 = Coordinate.create(190, 130);
        Stop stop60 = Stop.defaultStop("stop5", c020);

        s1.addStop(stop1);
        s1.addStop(stop2);
        s1.addStop(stop3);

        s2.addStop(stop4);
        s2.addStop(stop5);
        s2.addStop(stop50);
        s2.addStop(stop60);
        streets.add(s1);
        streets.add(s2);
        streets.add(s3);
        streets.add(s4);
        streets.add(s5);

        Line line1 = Line.defaultLine("10");
        Line line2 = Line.defaultLine("20");

        line1.addStreet(s1);
        line2.addStreet(s2);
        line2.addStreet(s3);
        line2.addStreet(s4);
        line2.addStreet(s5);
        lines.add(line1);
        lines.add(line2);
//
//        line1.addStop(stop1);
//        line1.addStop(stop2);
//        line1.addStop(stop3);
//        line1.addStop(stop4);
//        line1.addStop(stop5);



//        lines.add(line1);

//        firstBus = new Bus("firstBus", line1);
//
//        Thread.sleep(3000);
//        stop1.getCoordinate();
//        MoveTo move_to = new MoveTo();
//        move_to.setX(stop1.getCoordinate().getX());
//        move_to.setY(stop1.getCoordinate().getY());
//
//        Path path = new Path();
//        path.getElements().add(move_to);
//        PathTransition pathTransition = new PathTransition();
//        pathTransition.setDuration(Duration.millis(4000));
//        pathTransition.setPath(path);
//        pathTransition.setNode(firstBus);
//        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
//        pathTransition.setAutoReverse(true);
//        pathTransition.play();
//
//        firstBus.Move();

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
