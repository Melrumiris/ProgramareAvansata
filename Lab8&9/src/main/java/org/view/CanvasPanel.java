package org.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.model.Cell;
import org.model.MazeData;
import org.model.MazeGame;
import org.model.Robot;
import org.model.Wall;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CanvasPanel extends Canvas {

    public enum PickMode { NONE, START, END }

    private static final int    CELL_SIZE      = 40;
    private static final double HIT_THRESHOLD  = 6.0;

    private List<List<Cell>> grid;
    private final Random random = new Random();

    private Cell startCell = null;
    private Cell endCell   = null;
    private PickMode pickMode = PickMode.NONE;

    private Consumer<Cell> onStartCellPicked;
    private Consumer<Cell> onEndCellPicked;

    // --- Maze generation animation state ---
    private Timeline generationTimeline = null;
    private final Set<Cell> visitedCells = new HashSet<>();
    private Cell currentGenerationCell = null;

    // --- Live game rendering ---
    private MazeGame activeGame = null;
    private Timeline gameRenderTimeline = null;

    // --- Public API ---

    public CanvasPanel() {
        setOnMouseClicked(this::handleClick);
    }

    public void setPickMode(PickMode mode) { this.pickMode = mode; }
    public PickMode getPickMode()          { return pickMode; }

    public void setOnStartCellPicked(Consumer<Cell> cb) { this.onStartCellPicked = cb; }
    public void setOnEndCellPicked(Consumer<Cell> cb)   { this.onEndCellPicked = cb; }

    public Cell getStartCell() { return startCell; }
    public Cell getEndCell()   { return endCell; }

    public void setStartCell(int row, int col) {
        if (!isValidCell(row, col)) return;
        startCell = grid.get(row).get(col);
        draw();
    }

    public void setEndCell(int row, int col) {
        if (!isValidCell(row, col)) return;
        endCell = grid.get(row).get(col);
        draw();
    }

    public void initGrid(int rows, int cols) {
        grid = IntStream.range(0, rows)
                .mapToObj(r -> IntStream.range(0, cols)
                        .mapToObj(c -> new Cell(r, c))
                        .collect(Collectors.toCollection(ArrayList::new)))
                .collect(Collectors.toCollection(ArrayList::new));

        startCell = null;
        endCell   = null;
        pickMode  = PickMode.NONE;

        setWidth(cols  * CELL_SIZE);
        setHeight(rows * CELL_SIZE);
        draw();
    }

    public void generateMaze(long stepDelayMs, Runnable onComplete) {
        if (grid == null) return;

        // Stop any in-progress animation
        if (generationTimeline != null) {
            generationTimeline.stop();
            generationTimeline = null;
        }

        // Reset all walls without redrawing yet
        grid.stream().flatMap(List::stream).forEach(Cell::resetWalls);
        visitedCells.clear();
        currentGenerationCell = null;

        List<Runnable> steps = buildDFSSteps();

        if (steps.isEmpty()) {
            draw();
            if (onComplete != null) onComplete.run();
            return;
        }

        AtomicInteger index = new AtomicInteger(0);
        generationTimeline = new Timeline(
                new KeyFrame(Duration.millis(Math.max(1, stepDelayMs)), e -> {
                    int i = index.getAndIncrement();
                    if (i < steps.size()) steps.get(i).run();
                })
        );
        generationTimeline.setCycleCount(steps.size());
        generationTimeline.setOnFinished(e -> {
            currentGenerationCell = null;
            visitedCells.clear();
            generationTimeline = null;
            draw();
            if (onComplete != null) onComplete.run();
        });
        generationTimeline.play();
    }

    private List<Runnable> buildDFSSteps() {
        int rows = grid.size();
        int cols = grid.get(0).size();
        boolean[][] visited = new boolean[rows][cols];
        List<Runnable> steps = new ArrayList<>();

        Deque<Cell> stack = new ArrayDeque<>();
        Cell start = grid.get(0).get(0);
        visited[start.getRow()][start.getCol()] = true;
        visitedCells.add(start);
        stack.push(start);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();

            // Collect unvisited neighbours in a random order
            List<Wall> walls = new ArrayList<>(List.of(Wall.values()));
            Collections.shuffle(walls, random);

            Cell chosen = null;
            Wall chosenWall = null;
            for (Wall w : walls) {
                int nr = current.getRow() + w.rowDelta();
                int nc = current.getCol() + w.colDelta();
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && !visited[nr][nc]) {
                    chosen = grid.get(nr).get(nc);
                    chosenWall = w;
                    break;
                }
            }

            if (chosen != null) {
                visited[chosen.getRow()][chosen.getCol()] = true;
                final Cell from     = current;
                final Cell to       = chosen;
                final Wall wall     = chosenWall;
                stack.push(chosen);

                steps.add(() -> {
                    // Remove wall between from and to (both sides)
                    from.setWall(wall, false);
                    to.setWall(wall.opposite(), false);
                    visitedCells.add(to);
                    currentGenerationCell = to;
                    draw();
                });
            } else {
                stack.pop(); // backtrack – no Runnable emitted
            }
        }
        return steps;
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

        if (pickMode == PickMode.START) {
            startCell = grid.get(row).get(col);
            pickMode  = PickMode.NONE;
            draw();
            if (onStartCellPicked != null) onStartCellPicked.accept(startCell);
            return;
        }
        if (pickMode == PickMode.END) {
            endCell  = grid.get(row).get(col);
            pickMode = PickMode.NONE;
            draw();
            if (onEndCellPicked != null) onEndCellPicked.accept(endCell);
            return;
        }

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

    public Cell getExitCell() {
        if (grid == null) return null;
        Set<Cell> outside = findOutsideCells();
        return outside.isEmpty() ? null : outside.iterator().next();
    }

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
        drawGenerationOverlay(gc);
        drawStaticEmoji(gc);
        if (activeGame != null) drawEntities(gc);
    }

    public void setGame(MazeGame game) {
        this.activeGame = game;
        if (gameRenderTimeline != null) gameRenderTimeline.stop();
        gameRenderTimeline = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            draw();
            if (activeGame != null && activeGame.isGameOver()) {
                gameRenderTimeline.stop();
                draw(); // one final repaint to show end state
            }
        }));
        gameRenderTimeline.setCycleCount(Timeline.INDEFINITE);
        gameRenderTimeline.play();
    }

    public void stopGameRendering() {
        if (gameRenderTimeline != null) {
            gameRenderTimeline.stop();
            gameRenderTimeline = null;
        }
        activeGame = null;
        draw();
    }

    private void drawEntities(GraphicsContext gc) {
        gc.setFont(Font.font("Noto Emoji", CELL_SIZE * 0.75));
        gc.setFill(Color.BLACK);

        // Vertical baseline offset so the glyph sits inside the cell
        double ox = CELL_SIZE * 0.05;
        double oy = CELL_SIZE * 0.82;

        // Robots — get a snapshot to avoid holding the lock during rendering
        activeGame.getRobotPositions().forEach((cell, robot) ->
                gc.fillText("🤖",
                        cell.getCol() * CELL_SIZE + ox,
                        cell.getRow() * CELL_SIZE + oy));

        // Bunny
        Cell bunnyCell = activeGame.getBunnyCell();
        if (bunnyCell != null) {
            gc.fillText("🐰",
                    bunnyCell.getCol() * CELL_SIZE + ox,
                    bunnyCell.getRow() * CELL_SIZE + oy);
        }
    }

    private void drawCellBackgrounds(GraphicsContext gc) {
        grid.stream().flatMap(List::stream).forEach(cell -> {
            double x = cell.getCol() * CELL_SIZE;
            double y = cell.getRow() * CELL_SIZE;
            if (cell.equals(startCell)) {
                gc.setFill(Color.rgb(160, 110, 52));   // dirt brown
            } else if (cell.equals(currentGenerationCell) || visitedCells.contains(cell)) {
                gc.setFill(Color.rgb(162, 218, 88));   // bright freshly-mowed grass
            } else {
                gc.setFill(Color.rgb(120, 175, 55));   // grass green
            }
            gc.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        });
    }

    private void drawGenerationOverlay(GraphicsContext gc) {
        if (currentGenerationCell == null) return;
        gc.setFont(Font.font("Noto Emoji", CELL_SIZE * 0.75));
        gc.setFill(Color.BLACK);
        gc.fillText("🚜",
                currentGenerationCell.getCol() * CELL_SIZE + CELL_SIZE * 0.05,
                currentGenerationCell.getRow() * CELL_SIZE + CELL_SIZE * 0.82);
    }

    private void drawStaticEmoji(GraphicsContext gc) {
        if (endCell == null) return;
        gc.setFont(Font.font("Noto Emoji", CELL_SIZE * 0.75));
        gc.setFill(Color.BLACK);
        gc.fillText("🏁",
                endCell.getCol() * CELL_SIZE + CELL_SIZE * 0.05,
                endCell.getRow() * CELL_SIZE + CELL_SIZE * 0.82);
    }

    private void drawWalls(GraphicsContext gc) {
        gc.setStroke(Color.rgb(25, 90, 25));   // deep bush green
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

    public boolean isValid() {
        if (grid == null || startCell == null || endCell == null) return false;

        // BFS from startCell — maze is valid iff endCell is reachable
        Set<Cell> visited = new HashSet<>();
        java.util.Queue<Cell> queue = new java.util.ArrayDeque<>();
        queue.add(startCell);
        visited.add(startCell);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current.equals(endCell)) return true;
            int r = current.getRow();
            int c = current.getCol();
            for (Wall wall : Wall.values()) {
                if (current.hasWall(wall)) continue;
                int nr = r + wall.rowDelta();
                int nc = c + wall.colDelta();
                if (!isValidCell(nr, nc)) continue;
                Cell neighbor = grid.get(nr).get(nc);
                if (visited.add(neighbor)) queue.add(neighbor);
            }
        }
        return false;
    }

    public void validateMaze() {
        if (grid == null) return;
        draw();
    }

    // --- Export / Serialization ---

    public void exportPng(File file) {
        if (grid == null) return;
        WritableImage image = snapshot(null, null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void saveMaze(File file) {
        if (grid == null) return;
        int sr = startCell != null ? startCell.getRow() : -1;
        int sc = startCell != null ? startCell.getCol() : -1;
        int er = endCell   != null ? endCell.getRow()   : -1;
        int ec = endCell   != null ? endCell.getCol()   : -1;
        MazeData data = new MazeData(grid, sr, sc, er, ec);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void loadMaze(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            MazeData data = (MazeData) ois.readObject();
            if (data == null || data.grid == null || data.grid.isEmpty()) return;
            grid = data.grid;
            int rows = grid.size();
            int cols = grid.get(0).size();
            setWidth(cols  * CELL_SIZE);
            setHeight(rows * CELL_SIZE);

            // Restore start cell
            if (data.startRow >= 0 && isValidCell(data.startRow, data.startCol)) {
                startCell = grid.get(data.startRow).get(data.startCol);
                if (onStartCellPicked != null) onStartCellPicked.accept(startCell);
            } else {
                startCell = null;
            }

            // Restore end cell
            if (data.endRow >= 0 && isValidCell(data.endRow, data.endCol)) {
                endCell = grid.get(data.endRow).get(data.endCol);
                if (onEndCellPicked != null) onEndCellPicked.accept(endCell);
            } else {
                endCell = null;
            }

            draw();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
