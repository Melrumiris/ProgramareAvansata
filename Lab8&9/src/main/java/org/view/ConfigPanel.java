package org.view;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ConfigPanel extends HBox {

    private static final int MIN_DIM     = 2;
    private static final int MAX_DIM     = 50;
    private static final int DEFAULT_DIM = 10;

    private final Spinner<Integer> rowsSpinner;
    private final Spinner<Integer> colsSpinner;

    // Start cell controls
    private final ToggleButton pickStartBtn = new ToggleButton("Pick Start");
    private final Spinner<Integer> startRowSpinner;
    private final Spinner<Integer> startColSpinner;

    // End cell controls
    private final ToggleButton pickEndBtn = new ToggleButton("Pick End");
    private final Spinner<Integer> endRowSpinner;
    private final Spinner<Integer> endColSpinner;

    // Game configuration
    private final Spinner<Integer> robotCountSpinner;
    private final Spinner<Integer> bunnyCountSpinner;
    private final CheckBox         manualModeCheck;

    private Runnable onConfigChange;

    public ConfigPanel(CanvasPanel canvas, Stage stage) {
        super(12);
        setPadding(new Insets(8));
        setAlignment(Pos.CENTER_LEFT);

        // --- Grid size row ---
        rowsSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_DIM, MAX_DIM, DEFAULT_DIM));
        colsSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(MIN_DIM, MAX_DIM, DEFAULT_DIM));
        rowsSpinner.setPrefWidth(70);
        colsSpinner.setPrefWidth(70);

        Button drawButton = new Button("Draw Grid");
        drawButton.setOnAction(e -> {
            canvas.initGrid(getSelectedRows(), getSelectedCols());
            stage.sizeToScene();
        });

        // --- Start cell row ---
        startRowSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MAX_DIM - 1, 0));
        startColSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MAX_DIM - 1, 0));
        startRowSpinner.setPrefWidth(65);
        startColSpinner.setPrefWidth(65);
        startRowSpinner.setEditable(true);
        startColSpinner.setEditable(true);

        pickStartBtn.setOnAction(e -> {
            if (pickStartBtn.isSelected()) {
                pickEndBtn.setSelected(false);
                canvas.setPickMode(CanvasPanel.PickMode.START);
            } else {
                canvas.setPickMode(CanvasPanel.PickMode.NONE);
            }
        });

        startRowSpinner.valueProperty().addListener((obs, o, n) -> {
            if (canvas.getStartCell() == null ||
                    canvas.getStartCell().getRow() != n) {
                canvas.setStartCell(n, startColSpinner.getValue());
            }
        });
        startColSpinner.valueProperty().addListener((obs, o, n) -> {
            if (canvas.getStartCell() == null ||
                    canvas.getStartCell().getCol() != n) {
                canvas.setStartCell(startRowSpinner.getValue(), n);
            }
        });

        canvas.setOnStartCellPicked(cell -> {
            pickStartBtn.setSelected(false);
            startRowSpinner.getValueFactory().setValue(cell.getRow());
            startColSpinner.getValueFactory().setValue(cell.getCol());
        });

        HBox gridRow = new HBox(8,
                new Label("Rows:"), rowsSpinner,
                new Label("Cols:"), colsSpinner,
                drawButton);
        gridRow.setAlignment(Pos.CENTER_LEFT);

        HBox startRow = new HBox(8,
                pickStartBtn,
                new Label("Row:"), startRowSpinner,
                new Label("Col:"), startColSpinner);
        startRow.setAlignment(Pos.CENTER_LEFT);

        // --- End cell row ---
        endRowSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MAX_DIM - 1, 0));
        endColSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, MAX_DIM - 1, 0));
        endRowSpinner.setPrefWidth(65);
        endColSpinner.setPrefWidth(65);
        endRowSpinner.setEditable(true);
        endColSpinner.setEditable(true);

        pickEndBtn.setOnAction(e -> {
            if (pickEndBtn.isSelected()) {
                pickStartBtn.setSelected(false);
                canvas.setPickMode(CanvasPanel.PickMode.END);
            } else {
                canvas.setPickMode(CanvasPanel.PickMode.NONE);
            }
        });

        endRowSpinner.valueProperty().addListener((obs, o, n) -> {
            if (canvas.getEndCell() == null ||
                    canvas.getEndCell().getRow() != n) {
                canvas.setEndCell(n, endColSpinner.getValue());
            }
        });
        endColSpinner.valueProperty().addListener((obs, o, n) -> {
            if (canvas.getEndCell() == null ||
                    canvas.getEndCell().getCol() != n) {
                canvas.setEndCell(endRowSpinner.getValue(), n);
            }
        });

        canvas.setOnEndCellPicked(cell -> {
            pickEndBtn.setSelected(false);
            endRowSpinner.getValueFactory().setValue(cell.getRow());
            endColSpinner.getValueFactory().setValue(cell.getCol());
        });

        HBox endRow = new HBox(8,
                pickEndBtn,
                new Label("Row:"), endRowSpinner,
                new Label("Col:"), endColSpinner);
        endRow.setAlignment(Pos.CENTER_LEFT);

        // --- Game configuration row ---
        robotCountSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9, 3));
        bunnyCountSpinner = new Spinner<>(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        robotCountSpinner.setPrefWidth(60);
        bunnyCountSpinner.setPrefWidth(60);

        manualModeCheck = new CheckBox("Manual bunny");

        // Fire config-change event whenever these values change
        robotCountSpinner.valueProperty().addListener((obs, o, n) -> fireConfigChange());
        bunnyCountSpinner.valueProperty().addListener((obs, o, n) -> fireConfigChange());
        manualModeCheck.selectedProperty().addListener((obs, o, n) -> fireConfigChange());

        HBox gameConfigRow = new HBox(8,
                new Label("Robots:"), robotCountSpinner,
                new Label("Bunnies:"), bunnyCountSpinner,
                manualModeCheck);
        gameConfigRow.setAlignment(Pos.CENTER_LEFT);

        Separator sep1 = new Separator(Orientation.VERTICAL);
        Separator sep2 = new Separator(Orientation.VERTICAL);
        Separator sep3 = new Separator(Orientation.VERTICAL);

        getChildren().addAll(gridRow, sep1, startRow, sep2, endRow, sep3, gameConfigRow);
    }

    // --- Accessors ---

    public int     getSelectedRows() { return rowsSpinner.getValue(); }
    public int     getSelectedCols() { return colsSpinner.getValue(); }
    public int     getNumRobots()    { return robotCountSpinner.getValue(); }
    public int     getBunnyCount()   { return bunnyCountSpinner.getValue(); }
    public boolean isManualMode()    { return manualModeCheck.isSelected(); }

    /** Register a callback invoked whenever robot count, bunny count, or manual mode changes. */
    public void setOnConfigChange(Runnable cb) { this.onConfigChange = cb; }

    private void fireConfigChange() {
        if (onConfigChange != null) onConfigChange.run();
    }
}

