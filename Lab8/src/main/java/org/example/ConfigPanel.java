package org.example;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ConfigPanel extends HBox {

    private final Spinner<Integer> rowsSpinner;
    private final Spinner<Integer> colsSpinner;

    public ConfigPanel(CanvasPanel canvas, Stage stage) {
        super(10);
        setPadding(new Insets(8));

        rowsSpinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 50, 10));
        colsSpinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 50, 10));
        rowsSpinner.setPrefWidth(70);
        colsSpinner.setPrefWidth(70);

        Button drawButton = new Button("Draw Grid");
        drawButton.setOnAction(e -> {
            canvas.initGrid(rowsSpinner.getValue(), colsSpinner.getValue());
            stage.sizeToScene();
        });

        getChildren().addAll(
                new Label("Rows:"), rowsSpinner,
                new Label("Cols:"), colsSpinner,
                drawButton
        );
    }
}
