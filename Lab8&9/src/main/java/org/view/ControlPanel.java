package org.view;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.model.MazeGame;

import java.io.File;

public class ControlPanel extends HBox {

    public ControlPanel(CanvasPanel canvas, Stage stage) {
        super(10);
        setPadding(new Insets(8));
        setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        statusLabel.setVisible(false);

        PauseTransition hideTimer = new PauseTransition(Duration.seconds(5));
        hideTimer.setOnFinished(e -> statusLabel.setVisible(false));

        Button validateBtn = createButton("Validate", () -> {
            canvas.validateMaze();
            boolean valid = canvas.isValid();
            String msg = canvas.getStartCell() == null || canvas.getEndCell() == null
                    ? "✘ Set a start and end cell first!"
                    : valid ? "✔ Valid maze – path exists!" : "✘ Invalid maze – no path found!";
            statusLabel.setText(msg);
            statusLabel.setTextFill(valid ? Color.rgb(0, 140, 0) : Color.rgb(200, 0, 0));
            statusLabel.setVisible(true);
            hideTimer.playFromStart();
        });

        // Export PNG button
        Button exportBtn = createButton("Export PNG", () -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export Maze as PNG");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PNG Image", "*.png"));
            chooser.setInitialFileName("maze.png");
            File file = chooser.showSaveDialog(stage);
            if (file != null) canvas.exportPng(file);
        });

        // Save maze state button
        Button saveBtn = createButton("Save", () -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Maze State");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Maze File", "*.maze"));
            chooser.setInitialFileName("maze.maze");
            File file = chooser.showSaveDialog(stage);
            if (file != null) canvas.saveMaze(file);
        });

        // Load maze state button
        Button loadBtn = createButton("Load", () -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Load Maze State");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Maze File", "*.maze"));
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                canvas.loadMaze(file);
                stage.sizeToScene();
            }
        });

        // Play button – spawns one Bunny thread + robot threads
        Button simBtn = createButton("Play", () -> {
            if (canvas.getStartCell() == null || canvas.getEndCell() == null) {
                statusLabel.setText("✘ Set a start and end cell first!");
                statusLabel.setTextFill(Color.rgb(200, 0, 0));
                statusLabel.setVisible(true);
                hideTimer.playFromStart();
                return;
            }
            if (!canvas.isValid()) {
                statusLabel.setText("✘ No path from start to end!");
                statusLabel.setTextFill(Color.rgb(200, 0, 0));
                statusLabel.setVisible(true);
                hideTimer.playFromStart();
                return;
            }
            MazeGame game = new MazeGame(canvas.getGrid(), canvas.getStartCell(), canvas.getEndCell(), 3);
            game.start();
            statusLabel.setText("▶ Game running – see console");
            statusLabel.setTextFill(Color.rgb(0, 100, 200));
            statusLabel.setVisible(true);
            hideTimer.playFromStart();
        });

        getChildren().addAll(
                createButton("Create",   canvas::generateMaze),
                createButton("Reset",    canvas::resetWalls),
                validateBtn,
                exportBtn,
                saveBtn,
                loadBtn,
                simBtn,
                createButton("Exit",     Platform::exit),
                statusLabel
        );
    }

    private static Button createButton(String label, Runnable action) {
        Button btn = new Button(label);
        btn.setOnAction(e -> action.run());
        return btn;
    }
}
