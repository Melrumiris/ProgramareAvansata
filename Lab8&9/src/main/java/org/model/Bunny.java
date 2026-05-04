package org.model;

import org.ai.BFSExplorer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bunny extends Thread {

    private volatile long moveDelayMs = 1000;
    private volatile boolean paused = false;

    private Cell currentCell;
    private final MazeGame game;

    private final Set<Cell>   exploredCells = new HashSet<>();
    private final Deque<Cell> currentPath   = new ArrayDeque<>();

    public Bunny(Cell startCell, MazeGame game) {
        super("Bunny");
        this.currentCell = startCell;
        this.game = game;
        exploredCells.add(startCell);
        setDaemon(true);
    }

    // --- Speed / pause controls (called from external threads) ---

    public void pause()    { this.paused = true; }
    public void unpause()  { this.paused = false; }
    public void speedUp()  { moveDelayMs = Math.max(200,  moveDelayMs - 200); }
    public void slowDown() { moveDelayMs = Math.min(3000, moveDelayMs + 200); }

    // ---------------------------------------------------------------

    @Override
    public void run() {
        System.out.println("[Bunny] starts at (" +
                currentCell.getRow() + "," + currentCell.getCol() + ")");

        while (!game.isGameOver()) {
            Cell next = nextTarget();

            if (next != null) {
                if (game.tryMoveBunny(next)) {
                    currentCell = next;
                    exploredCells.add(next);
                    currentPath.poll();
                    // The bunny can see through any open wall from its current cell,
                    // so mark directly reachable neighbours as discovered.
                    discoverNeighbours();
                    // If we just spotted the exit, abandon the current plan so
                    // nextTarget() will immediately route toward it.
                    if (exploredCells.contains(game.getExitCell())) {
                        currentPath.clear();
                    }
                }
            }

            if (game.isGameOver()) break;

            try {
                waitForNextMove();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[Bunny] stopped.");
        game.announceResult();
    }

    /** Number of cells the bunny has explored so far. */
    public int getExploredCount() { return exploredCells.size(); }

    /** True once the bunny has spotted the exit (line-of-sight or stepped on it). */
    public boolean hasSpottedExit() { return exploredCells.contains(game.getExitCell()); }

    // ---------------------------------------------------------------

    /**
     * Marks all directly wall-accessible neighbours of the current cell as
     * explored (the bunny has line-of-sight through open passages even without
     * stepping onto them).
     */
    private void discoverNeighbours() {
        List<List<Cell>> grid = game.getGrid();
        int rows = grid.size(), cols = grid.get(0).size();
        for (Wall w : Wall.values()) {
            if (currentCell.hasWall(w)) continue;
            int nr = currentCell.getRow() + w.rowDelta();
            int nc = currentCell.getCol() + w.colDelta();
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols)
                exploredCells.add(grid.get(nr).get(nc));
        }
    }

    /**
     * Returns the next cell to move toward, planning a BFS path if needed.
     *
     * Strategy (limited knowledge — navigation restricted to explored cells):
     *   1. Follow the current planned path if one exists.
     *   2. If the exit has already been discovered, navigate directly there.
     *   3. Otherwise explore toward the nearest unvisited cell.
     */
    private Cell nextTarget() {
        if (!currentPath.isEmpty()) return currentPath.peek();

        Cell exit = game.getExitCell();

        // If we've already found the exit, head straight for it
        if (exploredCells.contains(exit)) {
            List<Cell> toExit = BFSExplorer.findPathInExplored(
                    currentCell, exit, exploredCells, game.getGrid());
            if (!toExit.isEmpty()) {
                currentPath.addAll(toExit);
                return currentPath.peek();
            }
        }

        // Explore the nearest cell we haven't visited yet
        List<Cell> path = BFSExplorer.findNearestUnvisited(
                currentCell, exploredCells, game.getGrid());
        if (!path.isEmpty()) {
            currentPath.addAll(path);
            return currentPath.peek();
        }

        return null; // fully explored, nowhere to go
    }

    /**
     * Sleeps until the configured delay has elapsed.
     * Respects {@link #paused}: while paused the deadline is continuously
     * reset so the full delay runs after resuming.
     * Throws {@link InterruptedException} if the thread is interrupted
     * (e.g. by {@link MazeGame#forceStop()}).
     */
    private void waitForNextMove() throws InterruptedException {
        long deadline = System.currentTimeMillis() + moveDelayMs;
        while (!game.isGameOver()) {
            if (paused) {
                Thread.sleep(50);
                deadline = System.currentTimeMillis() + moveDelayMs;
                continue;
            }
            if (System.currentTimeMillis() >= deadline) break;
            Thread.sleep(50);
        }
    }
}
