package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class CanvasPanel extends JPanel {

    private static final int CELL_SIZE = 40;
    private static final float WALL_REMOVAL_PROBABILITY = 0.4f;

    private Cell[][] grid;
    private final Random random = new Random();

    public void initGrid(int rows, int cols) {
        grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
        repaint();
    }

    public void generateMaze() {
        if (grid == null) return;

        int rows = grid.length;
        int cols = grid[0].length;

        // Remove horizontal boundaries between vertically adjacent cells
        for (int r = 0; r < rows - 1; r++) {
            for (int c = 0; c < cols; c++) {
                if (random.nextFloat() < WALL_REMOVAL_PROBABILITY) {
                    grid[r][c].bottom = false;
                    grid[r + 1][c].top = false;
                }
            }
        }

        // Remove vertical boundaries between horizontally adjacent cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols - 1; c++) {
                if (random.nextFloat() < WALL_REMOVAL_PROBABILITY) {
                    grid[r][c].right = false;
                    grid[r][c + 1].left = false;
                }
            }
        }

        repaint();
    }

    public void resetWalls() {
        if (grid == null) return;
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                cell.resetWalls();
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (grid == null) return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(2f));
        g2d.setColor(Color.DARK_GRAY);

        for (Cell[] row : grid) {
            for (Cell cell : row) {
                int x = cell.col * CELL_SIZE;
                int y = cell.row * CELL_SIZE;

                if (cell.top)    g2d.drawLine(x,             y,             x + CELL_SIZE, y            );
                if (cell.right)  g2d.drawLine(x + CELL_SIZE, y,             x + CELL_SIZE, y + CELL_SIZE);
                if (cell.bottom) g2d.drawLine(x,             y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
                if (cell.left)   g2d.drawLine(x,             y,             x,             y + CELL_SIZE);
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (grid == null) return new Dimension(400, 400);
        return new Dimension(grid[0].length * CELL_SIZE, grid.length * CELL_SIZE);
    }
}
