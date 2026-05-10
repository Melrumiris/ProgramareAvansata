package org.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import org.model.Bunny;
import org.model.Cell;
import org.model.ManualBunny;
import org.model.MazeGame;
import org.model.Robot;
import org.model.Wall;

import java.util.ArrayList;
import java.util.List;

/**
 * Collapsible sidebar panel that shows game configuration info, entity-selection
 * controls, speed/pause buttons, and (in manual mode) a WASD directional pad.
 *
 * The sidebar is always present in the layout; visibility is toggled by the
 * "☰" button in the ControlPanel. It works before, during, and after a game:
 *   - No game: info labels show configured values; control buttons are disabled.
 *   - Game running: buttons are enabled; a refresh Timeline keeps the sidebar live.
 *   - Game over: buttons disabled again; sidebar stays open.
 */
public class GameSidebarPanel extends VBox {

    // --- Game info labels ---
    private final Label robotsLabel  = new Label("Robots: —");
    private final Label bunniesLabel = new Label("Bunnies: —");
    private final Label modeLabel    = new Label("User controlled: —");

    // --- Entity selector ---
    private final ToggleGroup entityGroup = new ToggleGroup();
    private final FlowPane    entityBox   = new FlowPane(4, 4);

    // --- Control buttons ---
    private final Button pauseBtn    = new Button("⏸ Pause");
    private final Button resumeBtn   = new Button("▶ Resume");
    private final Button speedUpBtn  = new Button("⬆ Faster");
    private final Button slowDownBtn = new Button("⬇ Slower");

    // --- Manual WASD pad ---
    private final Button wBtn = new Button("W ↑");
    private final Button aBtn = new Button("← A");
    private final Button sBtn = new Button("S ↓");
    private final Button dBtn = new Button("D →");
    private VBox manualSection; // stored for show/hide

    // --- Result banner ---
    private final Label resultLabel = new Label();

    // --- State ---
    private MazeGame activeGame = null;
    private Timeline refreshTimeline = null;
    private Robot    selectedRobot  = null;
    private boolean  bunnySelected  = false;

    // -------------------------------------------------------------------------

    public GameSidebarPanel() {
        super(10);
        setPadding(new Insets(12, 10, 12, 10));
        setPrefWidth(190);
        setMinWidth(190);
        setStyle("-fx-background-color: #f4f4f4; " +
                 "-fx-border-color: #c0c0c0; -fx-border-width: 0 0 0 1;");

        // Title
        Label title = new Label("Game Controls");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));

        // ── Game Info ──
        Label infoTitle = new Label("Game Info");
        infoTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        VBox infoBox = new VBox(3, infoTitle, robotsLabel, bunniesLabel, modeLabel);

        // ── Entity Selector ──
        Label entityTitle = new Label("Select Entity");
        entityTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        entityBox.setPrefWrapLength(170);

        // ── Control Buttons ──
        Label controlTitle = new Label("Controls");
        controlTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        HBox pauseRow = new HBox(4, pauseBtn, resumeBtn);
        HBox speedRow = new HBox(4, speedUpBtn, slowDownBtn);
        VBox controlBox = new VBox(4, controlTitle, pauseRow, speedRow);

        // ── Manual WASD Pad ──
        Label manualTitle = new Label("Manual Control (WASD)");
        manualTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 12));
        GridPane pad = buildManualPad();
        manualSection = new VBox(4, manualTitle, pad);
        manualSection.setVisible(false);
        manualSection.setManaged(false);

        resultLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        resultLabel.setWrapText(true);
        resultLabel.setVisible(false);
        resultLabel.setManaged(false);

        getChildren().addAll(
                title,
                new Separator(),
                infoBox,
                new Separator(),
                entityTitle, entityBox,
                new Separator(),
                controlBox,
                new Separator(),
                manualSection,
                new Separator(),
                resultLabel
        );

        setAllDisabled(true);
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Updates the game-info labels to reflect the given configuration.
     * Call this whenever config spinners change (before a game starts).
     */
    public void setConfigInfo(int numRobots, int numBunnies, boolean manualMode) {
        robotsLabel.setText("Robots: " + numRobots);
        bunniesLabel.setText("Bunnies: " + numBunnies);
        if (manualMode) {
            modeLabel.setText(numBunnies > 1 ? "User ctrl: first only" : "User ctrl: Yes");
        } else {
            modeLabel.setText("User ctrl: No");
        }
    }

    /**
     * Called when a new game starts. Enables all controls, populates robot
     * entity buttons, starts the game-over watcher, and shows the manual pad
     * if the game is in manual mode.
     *
     * @param game   the active game instance
     * @param canvas the canvas (needed by the manual pad to resolve grid cells)
     */
    public void setup(MazeGame game, CanvasPanel canvas) {
        this.activeGame   = game;
        this.selectedRobot = null;
        this.bunnySelected = false;

        // Populate entity toggle buttons
        entityBox.getChildren().clear();
        entityGroup.getToggles().clear();

        ToggleButton allBtn = makeEntityToggle("All");
        allBtn.setSelected(true);
        allBtn.setOnAction(e -> { selectedRobot = null; bunnySelected = false; });
        entityBox.getChildren().add(allBtn);

        ToggleButton bunnyBtn = makeEntityToggle("🐰 Bunny");
        bunnyBtn.setOnAction(e -> { selectedRobot = null; bunnySelected = true; });
        entityBox.getChildren().add(bunnyBtn);

        for (Robot r : game.getRobots()) {
            ToggleButton rb = makeEntityToggle(r.getName());
            rb.setOnAction(e -> { selectedRobot = r; bunnySelected = false; });
            entityBox.getChildren().add(rb);
        }

        // Wire control buttons
        pauseBtn.setOnAction(e    -> dispatch(Action.PAUSE));
        resumeBtn.setOnAction(e   -> dispatch(Action.RESUME));
        speedUpBtn.setOnAction(e  -> dispatch(Action.SPEED_UP));
        slowDownBtn.setOnAction(e -> dispatch(Action.SLOW_DOWN));

        // Manual pad
        boolean manual = game.isManualMode();
        manualSection.setVisible(manual);
        manualSection.setManaged(manual);
        if (manual && game.getManualBunny() != null) {
            ManualBunny mb = game.getManualBunny();
            wBtn.setOnAction(e -> offerManualMove(mb, Wall.TOP,    canvas));
            aBtn.setOnAction(e -> offerManualMove(mb, Wall.LEFT,   canvas));
            sBtn.setOnAction(e -> offerManualMove(mb, Wall.BOTTOM, canvas));
            dBtn.setOnAction(e -> offerManualMove(mb, Wall.RIGHT,  canvas));
        }

        // Update info labels with actual game values
        setConfigInfo(game.getRobots().size(), game.getBunnies().size(), game.isManualMode());

        // Reset result banner
        resultLabel.setVisible(false);
        resultLabel.setManaged(false);

        setAllDisabled(false);

        // Watch for game-over to auto-disable
        if (refreshTimeline != null) refreshTimeline.stop();
        refreshTimeline = new Timeline(new KeyFrame(Duration.millis(200), ev -> {
            if (activeGame != null && activeGame.isGameOver()) {
                MazeGame.State s   = activeGame.getState();
                String        msg  = activeGame.getResultMessage();
                teardown();
                showResult(s, msg);
            }
        }));
        refreshTimeline.setCycleCount(Timeline.INDEFINITE);
        refreshTimeline.play();
    }

    /**
     * Called when the game ends. Disables controls but leaves the sidebar open
     * so the user can still read the game info.
     */
    public void teardown() {
        activeGame = null;
        if (refreshTimeline != null) {
            refreshTimeline.stop();
            refreshTimeline = null;
        }
        setAllDisabled(true);
    }

    private void showResult(MazeGame.State state, String message) {
        String banner = switch (state) {
            case BUNNY_ESCAPED -> "\uD83D\uDC30 Bunnies Win! \uD83C\uDF89";
            case BUNNY_CAUGHT  -> "\uD83E\uDD16 Robots Win!";
            case TIMEOUT       -> "\u23F0 Time\u2019s Up!";
            default            -> message.isBlank() ? "Game Over" : message;
        };
        Color color = switch (state) {
            case BUNNY_ESCAPED -> Color.rgb(0, 150, 0);
            case BUNNY_CAUGHT  -> Color.rgb(180, 0, 0);
            default            -> Color.rgb(160, 100, 0);
        };
        resultLabel.setText(banner);
        resultLabel.setTextFill(color);
        resultLabel.setVisible(true);
        resultLabel.setManaged(true);
    }

    // -------------------------------------------------------------------------
    // Internals
    // -------------------------------------------------------------------------

    private GridPane buildManualPad() {
        GridPane pad = new GridPane();
        pad.setHgap(4);
        pad.setVgap(4);
        pad.setAlignment(Pos.CENTER);
        for (Button b : new Button[]{wBtn, aBtn, sBtn, dBtn}) {
            b.setPrefWidth(56);
        }
        pad.add(wBtn, 1, 0);
        pad.add(aBtn, 0, 1);
        pad.add(sBtn, 1, 1);
        pad.add(dBtn, 2, 1);
        return pad;
    }

    private void offerManualMove(ManualBunny mb, Wall dir, CanvasPanel canvas) {
        Cell cur = mb.getCurrentCell();
        if (cur == null || cur.hasWall(dir)) return;
        List<List<Cell>> grid = canvas.getGrid();
        int nr = cur.getRow() + dir.rowDelta();
        int nc = cur.getCol() + dir.colDelta();
        if (nr >= 0 && nr < grid.size() && nc >= 0 && nc < grid.get(0).size()) {
            mb.offerMove(grid.get(nr).get(nc));
        }
    }

    private ToggleButton makeEntityToggle(String label) {
        ToggleButton tb = new ToggleButton(label);
        tb.setToggleGroup(entityGroup);
        return tb;
    }

    private enum Action { PAUSE, RESUME, SPEED_UP, SLOW_DOWN }

    private void dispatch(Action action) {
        if (activeGame == null) return;
        List<Bunny>  allBunnies = activeGame.getBunnies();
        List<Robot>  allRobots  = activeGame.getRobots();

        if (selectedRobot != null) {
            apply(action, selectedRobot, null, null);
        } else if (bunnySelected) {
            apply(action, null, allBunnies, null);
        } else {
            apply(action, null, allBunnies, allRobots);
        }
    }

    private void apply(Action action, Robot robot,
                       List<Bunny> bunnies, List<Robot> robots) {
        switch (action) {
            case PAUSE     -> {
                if (robot  != null) robot.pause();
                if (bunnies != null) bunnies.forEach(Bunny::pause);
                if (robots  != null) robots.forEach(Robot::pause);
            }
            case RESUME    -> {
                if (robot  != null) robot.unpause();
                if (bunnies != null) bunnies.forEach(Bunny::unpause);
                if (robots  != null) robots.forEach(Robot::unpause);
            }
            case SPEED_UP  -> {
                if (robot  != null) robot.speedUp();
                if (bunnies != null) bunnies.forEach(Bunny::speedUp);
                if (robots  != null) robots.forEach(Robot::speedUp);
            }
            case SLOW_DOWN -> {
                if (robot  != null) robot.slowDown();
                if (bunnies != null) bunnies.forEach(Bunny::slowDown);
                if (robots  != null) robots.forEach(Robot::slowDown);
            }
        }
    }

    private void setAllDisabled(boolean disabled) {
        pauseBtn.setDisable(disabled);
        resumeBtn.setDisable(disabled);
        speedUpBtn.setDisable(disabled);
        slowDownBtn.setDisable(disabled);
        wBtn.setDisable(disabled);
        aBtn.setDisable(disabled);
        sBtn.setDisable(disabled);
        dBtn.setDisable(disabled);
        for (Node n : new ArrayList<>(entityBox.getChildren())) {
            n.setDisable(disabled);
        }
    }
}
