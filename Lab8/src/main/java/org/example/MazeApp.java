package org.example;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MazeApp {

    public static void setup(Stage stage) {
        stage.setTitle("Maze Generator");

        CanvasPanel canvas = new CanvasPanel();
        ConfigPanel configPanel = new ConfigPanel(canvas, stage);
        ControlPanel controlPanel = new ControlPanel(canvas);

        StackPane canvasWrapper = new StackPane(canvas);
        StackPane.setAlignment(canvas, Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(canvasWrapper);
        scrollPane.setPrefSize(600, 500);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        BorderPane root = new BorderPane();
        root.setTop(configPanel);
        root.setCenter(scrollPane);
        root.setBottom(controlPanel);

        stage.setScene(new Scene(root));
    }
}
