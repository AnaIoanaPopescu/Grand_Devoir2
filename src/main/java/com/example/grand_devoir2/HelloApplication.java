package com.example.grand_devoir2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/com/example/grand_devoir2/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 810, 790);
        scene.getStylesheets().add(getClass().getResource("/com/example/grand_devoir2/style.css").toExternalForm());

        stage.setTitle("Grand Devoir 2");  
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}