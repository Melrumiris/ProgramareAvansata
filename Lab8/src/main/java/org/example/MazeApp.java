package org.example;

import javax.swing.*;
import java.awt.*;

public class MazeApp extends JFrame {

    public MazeApp() {
        super("Maze Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        CanvasPanel canvas = new CanvasPanel();
        ConfigPanel configPanel = new ConfigPanel(canvas, this);
        ControlPanel controlPanel = new ControlPanel(canvas);

        JScrollPane scrollPane = new JScrollPane(canvas);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        setLayout(new BorderLayout());
        add(configPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }
}
