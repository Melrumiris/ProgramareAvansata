package org.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ConfigPanel extends HBox {

    private static final int MIN_DIM     = 2;
    private static final int MAX_DIM     = 50;
    private static final int DEFAULT_DIM = 10;

    private final Spinner<Integer> rowsSpinner;
    private final Spinner<Integer> colsSpinner;

    public ConfigPanel(CanvasPanel canvas, Stage stage) {
        super(10);
        setPadding(new Insets(8));

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

        getChildren().addAll(
                new Label("Rows:"), rowsSpinner,
                new Label("Cols:"), colsSpinner,
                drawButton
        );
    }

    public int getSelectedRows() { return rowsSpinner.getValue(); }
    public int getSelectedCols() { return colsSpinner.getValue(); }
}

