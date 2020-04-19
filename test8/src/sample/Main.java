package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main{

    public static void main(String[] args) throws IOException, InterruptedException {
//        BackEnd myBackEnd = new BackEnd();
//        myBackEnd.Back();
//        new FrontEnd().run();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(new FrontEnd());

        new BackEnd().Back();

        System.out.println("main ended!");
    }
}
