package org.example;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class CanvasPanel extends Canvas {

    private static final int CELL_SIZE = 40;
    private static final double WALL_REMOVAL_PROBABILITY = 0.4;

    private Cell[][] grid;
    private final Random random = new Random();

    public void initGrid(int rows, int cols) {
        grid = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Cell(r, c);
            }
        }
        setWidth(cols * CELL_SIZE);
        setHeight(rows * CELL_SIZE);
        draw();
    }

    public void generateMaze() {
        if (grid == null) return;

        for (Cell[] row : grid)
            for (Cell cell : row)
                cell.resetWalls();

        int rows = grid.length;
        int cols = grid[0].length;

        // Remove horizontal boundaries between vertically adjacent cells
        for (int r = 0; r < rows - 1; r++) {
            for (int c = 0; c < cols; c++) {
                if (random.nextDouble() < WALL_REMOVAL_PROBABILITY) {
                    grid[r][c].bottom = false;
                    grid[r + 1][c].top = false;
                }
            }
        }

        // Remove vertical boundaries between horizontally adjacent cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols - 1; c++) {
                if (random.nextDouble() < WALL_REMOVAL_PROBABILITY) {
                    grid[r][c].right = false;
                    grid[r][c + 1].left = false;
                }
            }
        }

        draw();
    }

    public void resetWalls() {
        if (grid == null) return;
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                cell.resetWalls();
            }
        }
        draw();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        if (grid == null) return;

        // Draw cell backgrounds
        gc.setFill(Color.rgb(220, 220, 220));
        for (Cell[] row : grid) {
            for (Cell cell : row) {
                double x = cell.col * CELL_SIZE;
                double y = cell.row * CELL_SIZE;
                gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        // Draw walls
        gc.setStroke(Color.rgb(80, 80, 80));
        gc.setLineWidth(3.0);

        for (Cell[] row : grid) {
            for (Cell cell : row) {
                double x = cell.col * CELL_SIZE;
                double y = cell.row * CELL_SIZE;

                if (cell.top)    gc.strokeLine(x,             y,             x + CELL_SIZE, y            );
                if (cell.right)  gc.strokeLine(x + CELL_SIZE, y,             x + CELL_SIZE, y + CELL_SIZE);
                if (cell.bottom) gc.strokeLine(x,             y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
                if (cell.left)   gc.strokeLine(x,             y,             x,             y + CELL_SIZE);
            }
        }
    }
}
