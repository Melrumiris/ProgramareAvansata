package org.example;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlPanel extends HBox {

    public ControlPanel(CanvasPanel canvas) {
        super(10);
        setPadding(new Insets(8));

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> canvas.generateMaze());

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> canvas.resetWalls());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> Platform.exit());

        getChildren().addAll(createButton, resetButton, exitButton);
    }
}
