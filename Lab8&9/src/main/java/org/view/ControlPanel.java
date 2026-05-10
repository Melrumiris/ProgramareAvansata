package org.view;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.model.Cell;
import org.model.GameManager;
import org.model.ManualBunny;
import org.model.MazeGame;
import org.model.RobotMemory;
import org.model.Wall;

import java.io.File;
import java.util.List;

/**
 * Bottom tool-bar (HBox). All maze tools sit left-to-right; the expand button
 * is pinned to the far right via a flexible spacer.
 */
public class ControlPanel extends HBox {

    private static final long TIME_LIMIT_MS      = 120_000;
    private static final long REPORT_INTERVAL_MS =  15_000;

    public ControlPanel(CanvasPanel canvas, Stage stage,
                        ConfigPanel configPanel, GameSidebarPanel sidebar) {
        super(8);
        setPadding(new Insets(8));
        setAlignment(Pos.CENTER_LEFT);

        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        statusLabel.setVisible(false);

        PauseTransition hideTimer = new PauseTransition(Duration.seconds(5));
        hideTimer.setOnFinished(e -> statusLabel.setVisible(false));

        // ── Speed slider ───────────────────────────────────────────────────
        Slider speedSlider = new Slider(1, 200, 50);
        speedSlider.setPrefWidth(90);
        speedSlider.setMajorTickUnit(100);
        speedSlider.setShowTickMarks(true);
        Label speedLabel = new Label("Speed");
        speedLabel.setFont(Font.font("SansSerif", 10));
        VBox speedBox = new VBox(2, speedLabel, speedSlider);
        speedBox.setAlignment(Pos.CENTER);

        // ── Create ─────────────────────────────────────────────────────────
        Button createBtn = new Button("Create");
        createBtn.setOnAction(e -> {
            long delayMs = (long) (201 - speedSlider.getValue());
            createBtn.setDisable(true);
            canvas.generateMaze(delayMs,
                    () -> Platform.runLater(() -> createBtn.setDisable(false)));
        });

        // ── Validate ───────────────────────────────────────────────────────
        Button validateBtn = btn("Validate", () -> {
            canvas.validateMaze();
            boolean valid = canvas.isValid();
            String msg = canvas.getStartCell() == null || canvas.getEndCell() == null
                    ? "\u2718 Set start & end first!"
                    : valid ? "\u2714 Valid \u2013 path exists!" : "\u2718 No path found!";
            statusLabel.setText(msg);
            statusLabel.setTextFill(valid ? Color.rgb(0, 140, 0) : Color.rgb(200, 0, 0));
            statusLabel.setVisible(true);
            hideTimer.playFromStart();
        });

        // ── Export PNG ─────────────────────────────────────────────────────
        Button exportBtn = btn("Export PNG", () -> {
            FileChooser fc = fileChooser("Export Maze as PNG", "PNG Image", "*.png", "maze.png");
            File f = fc.showSaveDialog(stage);
            if (f != null) canvas.exportPng(f);
        });

        // ── Save / Load ────────────────────────────────────────────────────
        Button saveBtn = btn("Save", () -> {
            FileChooser fc = fileChooser("Save Maze", "Maze File", "*.maze", "maze.maze");
            File f = fc.showSaveDialog(stage);
            if (f != null) canvas.saveMaze(f);
        });

        Button loadBtn = btn("Load", () -> {
            FileChooser fc = fileChooser("Load Maze", "Maze File", "*.maze", null);
            File f = fc.showOpenDialog(stage);
            if (f != null) { canvas.loadMaze(f); stage.sizeToScene(); }
        });

        // ── Play ───────────────────────────────────────────────────────────
        Button playBtn = new Button("\u25b6 Play");
        playBtn.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        playBtn.setOnAction(e -> startGame(
                canvas, stage, configPanel, sidebar, statusLabel, hideTimer));

        // ── Spacer → expand at far right ───────────────────────────────────
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ── Expand (☰) ────────────────────────────────────────────────────
        Button expandBtn = btn("\u2630", () -> {
            boolean show = !sidebar.isVisible();
            sidebar.setVisible(show);
            sidebar.setManaged(show);
            stage.sizeToScene();
        });
        expandBtn.setFont(Font.font("SansSerif", 14));

        getChildren().addAll(
                playBtn,
                btn("Reset", canvas::resetWalls),
                speedBox,
                createBtn,
                validateBtn,
                exportBtn,
                saveBtn,
                loadBtn,
                new Separator(),
                btn("Exit", Platform::exit),
                statusLabel,
                spacer,
                expandBtn
        );

        // Seed and keep sidebar info in sync with config spinners
        sidebar.setConfigInfo(configPanel.getNumRobots(),
                configPanel.getBunnyCount(), configPanel.isManualMode());
        configPanel.setOnConfigChange(() ->
                sidebar.setConfigInfo(configPanel.getNumRobots(),
                        configPanel.getBunnyCount(), configPanel.isManualMode()));
    }

    // -------------------------------------------------------------------------

    private void startGame(CanvasPanel canvas, Stage stage, ConfigPanel configPanel,
                           GameSidebarPanel sidebar, Label statusLabel,
                           PauseTransition hideTimer) {
        if (canvas.getStartCell() == null || canvas.getEndCell() == null) {
            statusLabel.setText("\u2718 Set a start and end cell first!");
            statusLabel.setTextFill(Color.rgb(200, 0, 0));
            statusLabel.setVisible(true);
            hideTimer.playFromStart();
            return;
        }
        if (!canvas.isValid()) {
            statusLabel.setText("\u2718 No path from start to end!");
            statusLabel.setTextFill(Color.rgb(200, 0, 0));
            statusLabel.setVisible(true);
            hideTimer.playFromStart();
            return;
        }

        int numRobots  = configPanel.getNumRobots();
        int numBunnies = configPanel.getBunnyCount();
        boolean manual = configPanel.isManualMode();

        RobotMemory sharedMemory = new RobotMemory();
        MazeGame game = new MazeGame(
                canvas.getGrid(), canvas.getStartCell(), canvas.getEndCell(),
                numRobots, numBunnies, manual, sharedMemory);
        game.start();
        canvas.setGame(game);
        new GameManager(game, TIME_LIMIT_MS, REPORT_INTERVAL_MS).start();
        sidebar.setup(game, canvas);

        // Watch for game-over and update the status bar with the final result
        Timeline[] watcher = new Timeline[1];
        watcher[0] = new Timeline(new KeyFrame(Duration.millis(300), ev -> {
            if (game.isGameOver()) {
                watcher[0].stop();
                hideTimer.stop();
                String result = switch (game.getState()) {
                    case BUNNY_ESCAPED -> "\uD83D\uDC30 Bunnies Win! \uD83C\uDF89";
                    case BUNNY_CAUGHT  -> "\uD83E\uDD16 Robots Win! Bunnies caught.";
                    case TIMEOUT       -> "\u23F0 Time\u2019s Up! No winner.";
                    default            -> "Game Over";
                };
                Color resultColor = switch (game.getState()) {
                    case BUNNY_ESCAPED -> Color.rgb(0, 140, 0);
                    case BUNNY_CAUGHT  -> Color.rgb(200, 0, 0);
                    default            -> Color.rgb(150, 90, 0);
                };
                statusLabel.setText(result);
                statusLabel.setTextFill(resultColor);
                statusLabel.setVisible(true);
            }
        }));
        watcher[0].setCycleCount(Timeline.INDEFINITE);
        watcher[0].play();
        if (manual && game.getManualBunny() != null) {
            ManualBunny mb = game.getManualBunny();
            stage.getScene().setOnKeyPressed(event -> {
                if (game.isGameOver()) {
                    stage.getScene().setOnKeyPressed(null);
                    return;
                }
                Wall dir = switch (event.getCode()) {
                    case W -> Wall.TOP;
                    case S -> Wall.BOTTOM;
                    case A -> Wall.LEFT;
                    case D -> Wall.RIGHT;
                    default -> null;
                };
                if (dir == null) return;
                Cell cur = mb.getCurrentCell();
                if (cur == null || cur.hasWall(dir)) return;
                List<List<Cell>> grid = canvas.getGrid();
                int nr = cur.getRow() + dir.rowDelta();
                int nc = cur.getCol() + dir.colDelta();
                if (nr >= 0 && nr < grid.size() && nc >= 0 && nc < grid.get(0).size()) {
                    mb.offerMove(grid.get(nr).get(nc));
                }
            });
        }

        statusLabel.setText("\u25b6 Game running!");
        statusLabel.setTextFill(Color.rgb(0, 100, 200));
        statusLabel.setVisible(true);
        hideTimer.playFromStart();
    }

    private static Button btn(String label, Runnable action) {
        Button b = new Button(label);
        b.setOnAction(e -> action.run());
        return b;
    }

    private static FileChooser fileChooser(String title, String desc,
                                           String ext, String initial) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(desc, ext));
        if (initial != null) fc.setInitialFileName(initial);
        return fc;
    }
}


