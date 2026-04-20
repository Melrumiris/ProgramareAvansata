package org.example;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    public ControlPanel(CanvasPanel canvas) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));

        JButton createButton = new JButton("Create");
        createButton.addActionListener(e -> canvas.generateMaze());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> canvas.resetWalls());

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));

        add(createButton);
        add(resetButton);
        add(exitButton);
    }
}
