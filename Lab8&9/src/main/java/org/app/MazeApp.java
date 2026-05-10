package org.app;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.view.CanvasPanel;
import org.view.ConfigPanel;
import org.view.ControlPanel;
import org.view.GameSidebarPanel;

public class MazeApp {

    private MazeApp() {}

    public static void setup(Stage stage) {
        stage.setTitle("Maze Generator");

        CanvasPanel      canvas       = new CanvasPanel();
        GameSidebarPanel sidebar      = new GameSidebarPanel();
        ConfigPanel      configPanel  = new ConfigPanel(canvas, stage);
        ControlPanel     controlPanel = new ControlPanel(canvas, stage, configPanel, sidebar);

        StackPane wrapper = new StackPane(canvas);
        StackPane.setAlignment(canvas, Pos.CENTER);

        ScrollPane scrollPane = new ScrollPane(wrapper);
        scrollPane.setPrefSize(600, 500);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Sidebar starts hidden; the "☰" button in ControlPanel toggles it
        sidebar.setVisible(false);
        sidebar.setManaged(false);

        BorderPane root = new BorderPane();
        root.setTop(configPanel);
        root.setCenter(scrollPane);
        root.setBottom(controlPanel);
        root.setRight(sidebar);

        stage.setScene(new Scene(root));
    }
}

