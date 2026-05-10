package org.model;

import org.ai.BFSExplorer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Robot extends Thread {

    /** Robots move at half the bunny's speed by default (bunny = 1000ms). */
    private volatile long moveDelayMs = 2000;

    private volatile boolean paused = false;

    private Cell currentCell;
    private final MazeGame game;
    private final RobotMemory sharedMemory;

    // --- BFS exploration state ---
    private final Set<Cell>   exploredCells = new HashSet<>();
    private final Deque<Cell> currentPath   = new ArrayDeque<>();

    public Robot(String name, Cell startCell, MazeGame game, RobotMemory sharedMemory) {
        super(name);
        this.currentCell  = startCell;
        this.game         = game;
        this.sharedMemory = sharedMemory;
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
        while (!game.isGameOver()) {
            // Synchronise explored cells with shared robot memory
            sharedMemory.addAllExplored(exploredCells);
            exploredCells.addAll(sharedMemory.getSharedExploredCellsSnapshot());

            // Check line-of-sight for any bunny
            scanForBunny();

            Cell next = nextTarget();

            if (next != null) {
                if (game.tryMoveRobot(this, currentCell, next)) {
                    currentCell = next;
                    exploredCells.add(next);
                    sharedMemory.addAllExplored(exploredCells);
                    currentPath.poll();
                } else {
                    // Blocked (another robot at target) — recalculate path
                    currentPath.clear();
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

        game.announceResult();
    }

    // ---------------------------------------------------------------

    /**
     * Scans all four straight corridors for line-of-sight to any active bunny.
     * If spotted, records the sighting in shared memory and triggers the ! alert.
     */
    private void scanForBunny() {
        List<List<Cell>> grid = game.getGrid();
        int rows = grid.size(), cols = grid.get(0).size();
        // Snapshot of all active bunny positions
        Map<Bunny, Cell> bunnyPositions = game.getBunnyPositions();
        if (bunnyPositions.isEmpty()) return;

        for (Wall dir : Wall.values()) {
            int r = currentCell.getRow();
            int c = currentCell.getCol();
            while (true) {
                Cell here = grid.get(r).get(c);
                if (here.hasWall(dir)) break;           // wall blocks LOS
                r += dir.rowDelta();
                c += dir.colDelta();
                if (r < 0 || r >= rows || c < 0 || c >= cols) break;
                Cell candidate = grid.get(r).get(c);
                // Check if any bunny is at this cell
                for (Cell bunnyCell : bunnyPositions.values()) {
                    if (candidate.equals(bunnyCell)) {
                        sharedMemory.reportBunnySighting(candidate);
                        game.markBunnySpotted(candidate);
                        return; // found one bunny — done scanning
                    }
                }
            }
        }
    }

    /**
     * Chooses the next move target using a priority chain:
     *   1. Continue along current planned path.
     *   2. Navigate to last known bunny location (using shared explored terrain).
     *   3. Explore nearest unvisited cell.
     *   4. Navigate to exit through explored terrain.
     */
    private Cell nextTarget() {
        // Priority 1: follow existing plan
        if (!currentPath.isEmpty()) return currentPath.peek();

        // Priority 2: head toward last known bunny location
        Cell lastKnown = sharedMemory.getLastKnownBunnyLocation();
        if (lastKnown != null && !lastKnown.equals(currentCell)) {
            Set<Cell> knownCells = sharedMemory.getSharedExploredCellsSnapshot();
            knownCells.addAll(exploredCells);
            List<Cell> toBunny = BFSExplorer.findPathInExplored(
                    currentCell, lastKnown, knownCells, game.getGrid());
            if (!toBunny.isEmpty()) {
                currentPath.addAll(toBunny);
                return currentPath.peek();
            }
        }

        // Priority 3: explore nearest unvisited cell
        List<Cell> path = BFSExplorer.findNearestUnvisited(
                currentCell, exploredCells, game.getGrid());
        if (!path.isEmpty()) {
            currentPath.addAll(path);
            return currentPath.peek();
        }

        // Priority 4: navigate to exit through all known terrain
        Set<Cell> knownCells = sharedMemory.getSharedExploredCellsSnapshot();
        knownCells.addAll(exploredCells);
        List<Cell> toExit = BFSExplorer.findPathInExplored(
                currentCell, game.getExitCell(), knownCells, game.getGrid());
        if (!toExit.isEmpty()) {
            currentPath.addAll(toExit);
            return currentPath.peek();
        }

        return null;
    }

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

