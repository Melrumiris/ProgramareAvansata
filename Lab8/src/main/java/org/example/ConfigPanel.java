package org.example;

import javax.swing.*;
import java.awt.*;

public class ConfigPanel extends JPanel {

    private final JSpinner rowsSpinner;
    private final JSpinner colsSpinner;

    public ConfigPanel(CanvasPanel canvas, JFrame parentFrame) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));

        rowsSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 50, 1));
        colsSpinner = new JSpinner(new SpinnerNumberModel(10, 2, 50, 1));

        add(new JLabel("Rows:"));
        add(rowsSpinner);
        add(new JLabel("Cols:"));
        add(colsSpinner);

        JButton drawButton = new JButton("Draw Grid");
        drawButton.addActionListener(e -> {
            canvas.initGrid(getRows(), getCols());
            parentFrame.pack();
        });
        add(drawButton);
    }

    public int getRows() {
        return (int) rowsSpinner.getValue();
    }

    public int getCols() {
        return (int) colsSpinner.getValue();
    }
}
