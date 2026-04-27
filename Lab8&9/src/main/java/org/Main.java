package org;

import javafx.application.Application;
import javafx.stage.Stage;
import org.app.MazeApp;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MazeApp.setup(primaryStage);
        primaryStage.show();
    }
}

