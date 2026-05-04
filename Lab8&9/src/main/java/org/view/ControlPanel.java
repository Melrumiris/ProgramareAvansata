package org.view;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.model.GameManager;
import org.model.MazeGame;
import org.model.Robot;

import java.io.File;
import java.util.List;

public class ControlPanel extends HBox {

    private static final long TIME_LIMIT_MS      = 120_000; // 2 minutes
    private static final long REPORT_INTERVAL_MS =  15_000; // 15 seconds

    public ControlPanel(CanvasPanel canvas, Stage stage) {
        super(10);
        setPadding(new Insets(8));
        setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 18));
        statusLabel.setVisible(false);

        PauseTransition hideTimer = new PauseTransition(Duration.seconds(5));
        hideTimer.setOnFinished(e -> statusLabel.setVisible(false));

        // --- Speed slider (1 = slowest 200ms, 200 = fastest 1ms) ---
        Slider speedSlider = new Slider(1, 200, 50);
        speedSlider.setPrefWidth(90);
        speedSlider.setMajorTickUnit(100);
        speedSlider.setShowTickMarks(true);

        Label speedLabel = new Label("Speed");
        speedLabel.setFont(Font.font("SansSerif", 11));

        VBox speedBox = new VBox(2, speedLabel, speedSlider);
        speedBox.setAlignment(Pos.CENTER);

        // --- Create button – triggers animated DFS maze generation ---
        Button createBtn = new Button("Create");
        createBtn.setOnAction(e -> {
            long delayMs = (long) (201 - speedSlider.getValue());
            createBtn.setDisable(true);
            canvas.generateMaze(delayMs, () -> Platform.runLater(() -> createBtn.setDisable(false)));
        });

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
            MazeGame game = new MazeGame(
                    canvas.getGrid(), canvas.getStartCell(), canvas.getEndCell(), 3);
            game.start();
            canvas.setGame(game);
            new GameManager(game, TIME_LIMIT_MS, REPORT_INTERVAL_MS).start();
            Scene scene = stage.getScene();
            if (scene != null) setupKeyboard(scene, game);
            statusLabel.setText("▶ Game running – see console");
            statusLabel.setTextFill(Color.rgb(0, 100, 200));
            statusLabel.setVisible(true);
            hideTimer.playFromStart();
        });

        getChildren().addAll(
                createBtn,
                speedBox,
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

    // Keyboard controls

    private void setupKeyboard(Scene scene, MazeGame game) {
        // Mutable selection state — use single-element arrays for lambda capture
        Robot[]  selectedRobot  = {null};
        boolean[] bunnySelected = {false};

        scene.setOnKeyPressed(event -> {
            if (game.isGameOver()) return;

            KeyCode code = event.getCode();

            // --- Entity selection (1-9 / B / ESC) ---
            String codeName = code.name();
            if (codeName.startsWith("DIGIT") && codeName.length() == 6) {
                int idx = (codeName.charAt(5) - '0') - 1;
                List<Robot> robots = game.getRobots();
                if (idx >= 0 && idx < robots.size()) {
                    selectedRobot[0]  = robots.get(idx);
                    bunnySelected[0]  = false;
                    System.out.println("[Keyboard] Selected " + selectedRobot[0].getName());
                }
                return;
            }

            switch (code) {
                case B -> {
                    bunnySelected[0] = true;
                    selectedRobot[0] = null;
                    System.out.println("[Keyboard] Selected Bunny");
                }
                case ESCAPE -> {
                    selectedRobot[0] = null;
                    bunnySelected[0] = false;
                    System.out.println("[Keyboard] Selected ALL");
                }
                case SPACE, P -> {
                    if (selectedRobot[0] != null) {
                        selectedRobot[0].pause();
                        System.out.println("[Keyboard] Paused " + selectedRobot[0].getName());
                    } else if (bunnySelected[0]) {
                        game.getBunny().pause();
                        System.out.println("[Keyboard] Paused Bunny");
                    } else {
                        game.getRobots().forEach(Robot::pause);
                        game.getBunny().pause();
                        System.out.println("[Keyboard] Paused ALL");
                    }
                }
                case R -> {
                    if (selectedRobot[0] != null) {
                        selectedRobot[0].unpause();
                        System.out.println("[Keyboard] Resumed " + selectedRobot[0].getName());
                    } else if (bunnySelected[0]) {
                        game.getBunny().unpause();
                        System.out.println("[Keyboard] Resumed Bunny");
                    } else {
                        game.getRobots().forEach(Robot::unpause);
                        game.getBunny().unpause();
                        System.out.println("[Keyboard] Resumed ALL");
                    }
                }
                case PLUS, EQUALS -> {
                    if (selectedRobot[0] != null) {
                        selectedRobot[0].speedUp();
                        System.out.println("[Keyboard] Speed up " + selectedRobot[0].getName());
                    } else if (bunnySelected[0]) {
                        game.getBunny().speedUp();
                        System.out.println("[Keyboard] Speed up Bunny");
                    } else {
                        game.getRobots().forEach(Robot::speedUp);
                        game.getBunny().speedUp();
                        System.out.println("[Keyboard] Speed up ALL");
                    }
                }
                case MINUS -> {
                    if (selectedRobot[0] != null) {
                        selectedRobot[0].slowDown();
                        System.out.println("[Keyboard] Slow down " + selectedRobot[0].getName());
                    } else if (bunnySelected[0]) {
                        game.getBunny().slowDown();
                        System.out.println("[Keyboard] Slow down Bunny");
                    } else {
                        game.getRobots().forEach(Robot::slowDown);
                        game.getBunny().slowDown();
                        System.out.println("[Keyboard] Slow down ALL");
                    }
                }
                default -> { /* ignore */ }
            }
        });

        System.out.println("[Keyboard] Controls active: 1-9=select robot, B=bunny, " +
                "ESC=all, SPACE/P=pause, R=unpause, +/-=speed");
    }
}
