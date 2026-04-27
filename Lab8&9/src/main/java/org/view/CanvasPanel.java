package org.view;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.model.Cell;
import org.model.Wall;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CanvasPanel extends Canvas {

    private static final int    CELL_SIZE                = 40;
    private static final double WALL_REMOVAL_PROBABILITY = 0.3;
    private static final double HIT_THRESHOLD            = 6.0;

    private List<List<Cell>> grid;
    private final Random random = new Random();

    // --- Public API ---

    public CanvasPanel() {
        setOnMouseClicked(this::handleClick);
    }

    public void initGrid(int rows, int cols) {
        grid = IntStream.range(0, rows)
                .mapToObj(r -> IntStream.range(0, cols)
                        .mapToObj(c -> new Cell(r, c))
                        .collect(Collectors.toCollection(ArrayList::new)))
                .collect(Collectors.toCollection(ArrayList::new));

        setWidth(cols  * CELL_SIZE);
        setHeight(rows * CELL_SIZE);
        draw();
    }

    public void generateMaze() {
        if (grid == null) return;
        resetWalls();

        int rows = grid.size();
        int cols = grid.get(0).size();

        IntStream.range(0, rows - 1).forEach(r ->
                IntStream.range(0, cols).forEach(c -> {
                    if (random.nextDouble() < WALL_REMOVAL_PROBABILITY)
                        removeWall(r, c, Wall.BOTTOM);
                }));

        IntStream.range(0, rows).forEach(r ->
                IntStream.range(0, cols - 1).forEach(c -> {
                    if (random.nextDouble() < WALL_REMOVAL_PROBABILITY)
                        removeWall(r, c, Wall.RIGHT);
                }));

        draw();
    }

    public void resetWalls() {
        if (grid == null) return;
        grid.stream().flatMap(List::stream).forEach(Cell::resetWalls);
        draw();
    }

    public void removeWall(int row, int col, Wall wall) {
        if (!isValidCell(row, col)) return;

        grid.get(row).get(col).setWall(wall, false);

        int neighborRow = row + wall.rowDelta();
        int neighborCol = col + wall.colDelta();
        if (isValidCell(neighborRow, neighborCol)) {
            grid.get(neighborRow).get(neighborCol).setWall(wall.opposite(), false);
        }

        draw();
    }

    /** Toggle a wall on/off; keeps the shared neighbour wall in sync. */
    public void toggleWall(int row, int col, Wall wall) {
        if (!isValidCell(row, col)) return;

        boolean newState = !grid.get(row).get(col).hasWall(wall);
        grid.get(row).get(col).setWall(wall, newState);

        int neighborRow = row + wall.rowDelta();
        int neighborCol = col + wall.colDelta();
        if (isValidCell(neighborRow, neighborCol)) {
            grid.get(neighborRow).get(neighborCol).setWall(wall.opposite(), newState);
        }

        draw();
    }

    // --- Mouse interaction ---

    private void handleClick(MouseEvent e) {
        if (grid == null) return;

        double x = e.getX();
        double y = e.getY();

        int col = (int) (x / CELL_SIZE);
        int row = (int) (y / CELL_SIZE);

        if (!isValidCell(row, col)) return;

        double cellX = x - col * CELL_SIZE;
        double cellY = y - row * CELL_SIZE;

        Wall hit = detectWall(cellX, cellY);
        if (hit != null) toggleWall(row, col, hit);
    }

    private Wall detectWall(double cellX, double cellY) {
        double distTop    = cellY;
        double distBottom = CELL_SIZE - cellY;
        double distLeft   = cellX;
        double distRight  = CELL_SIZE - cellX;

        double minDist = Math.min(Math.min(distTop, distBottom), Math.min(distLeft, distRight));

        if (minDist > HIT_THRESHOLD) return null;

        if (minDist == distTop)    return Wall.TOP;
        if (minDist == distBottom) return Wall.BOTTOM;
        if (minDist == distLeft)   return Wall.LEFT;
        return Wall.RIGHT;
    }

    // --- Accessors ---

    public List<List<Cell>> getGrid() { return grid; }
    public int getRows() { return grid == null ? 0 : grid.size(); }
    public int getCols() { return grid == null ? 0 : grid.get(0).size(); }

    // --- Private helpers ---

    private boolean isValidCell(int row, int col) {
        return grid != null
                && row >= 0 && row < grid.size()
                && col >= 0 && col < grid.get(0).size();
    }

    private void draw() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        if (grid == null) return;
        drawCellBackgrounds(gc);
        drawWalls(gc);
    }

    private void drawCellBackgrounds(GraphicsContext gc) {
        gc.setFill(Color.rgb(220, 220, 220));
        grid.stream().flatMap(List::stream).forEach(cell -> {
            double x = cell.getCol() * CELL_SIZE;
            double y = cell.getRow() * CELL_SIZE;
            gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        });
    }

    private void drawWalls(GraphicsContext gc) {
        gc.setStroke(Color.rgb(80, 80, 80));
        gc.setLineWidth(3.0);

        grid.stream().flatMap(List::stream).forEach(cell -> {
            double x = cell.getCol() * CELL_SIZE;
            double y = cell.getRow() * CELL_SIZE;

            if (cell.isTopWall())    gc.strokeLine(x,             y,             x + CELL_SIZE, y            );
            if (cell.isRightWall())  gc.strokeLine(x + CELL_SIZE, y,             x + CELL_SIZE, y + CELL_SIZE);
            if (cell.isBottomWall()) gc.strokeLine(x,             y + CELL_SIZE, x + CELL_SIZE, y + CELL_SIZE);
            if (cell.isLeftWall())   gc.strokeLine(x,             y,             x,             y + CELL_SIZE);
        });
    }

    public Set<Cell> findOutsideCells() {
        int lastRow = grid.size() - 1;
        return grid.stream()
                .flatMap(List::stream)
                .filter(cell -> cell.getRow() == 0 && !cell.isTopWall() || cell.getRow() == lastRow && !cell.isBottomWall()
                        || cell.getCol() == 0 && !cell.isLeftWall() || cell.getCol() == grid.get(cell.getRow()).size() - 1 && cell.isRightWall())
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Returns true if there is a path (through open walls) between at least
     * two distinct outside cells, i.e. the maze has a valid entrance/exit pair.
     */
    public boolean isValid() {
        if (grid == null) return false;

        Set<Cell> outside = findOutsideCells();
        if (outside.size() < 2) return false;

        // BFS from the first outside cell; check if any other outside cell is reachable
        Cell start = outside.iterator().next();

        Set<Cell> visited = new HashSet<>();
        java.util.Queue<Cell> queue = new java.util.ArrayDeque<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            int r = current.getRow();
            int c = current.getCol();

            for (Wall wall : Wall.values()) {
                if (current.hasWall(wall)) continue; // wall blocks passage
                int nr = r + wall.rowDelta();
                int nc = c + wall.colDelta();
                if (!isValidCell(nr, nc)) continue;
                Cell neighbor = grid.get(nr).get(nc);
                if (visited.contains(neighbor)) continue;
                visited.add(neighbor);
                queue.add(neighbor);
            }
        }

        // Valid if at least one *other* outside cell was reached
        return outside.stream().anyMatch(cell -> cell != start && visited.contains(cell));
    }

    /** Redraws the maze cleanly (result is displayed externally). */
    public void validateMaze() {
        if (grid == null) return;
        draw();
    }

    // --- Export / Serialization ---

    /**
     * Saves a snapshot of the current canvas as a PNG file.
     *
     * @param file destination file (should end with .png)
     */
    public void exportPng(File file) {
        if (grid == null) return;
        WritableImage image = snapshot(null, null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Serialises the current grid state to a file.
     *
     * @param file destination .maze file
     */
    public void saveMaze(File file) {
        if (grid == null) return;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(grid);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Restores a previously saved grid from a file.
     *
     * @param file source .maze file
     */
    @SuppressWarnings("unchecked")
    public void loadMaze(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<List<Cell>> loaded = (List<List<Cell>>) ois.readObject();
            if (loaded == null || loaded.isEmpty()) return;
            grid = loaded;
            int rows = grid.size();
            int cols = grid.get(0).size();
            setWidth(cols  * CELL_SIZE);
            setHeight(rows * CELL_SIZE);
            draw();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
