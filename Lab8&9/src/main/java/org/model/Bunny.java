package org.model;

import org.ai.BFSExplorer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Bunny extends Thread {

    /** Default move delay; used by BunnySpawner for inter-spawn timing. */
    static final long DEFAULT_DELAY_MS = 1000;

    protected volatile long moveDelayMs = DEFAULT_DELAY_MS;
    protected volatile boolean paused = false;

    /** Set by MazeGame when this specific bunny is caught or escapes. */
    volatile boolean resolved = false;

    protected Cell currentCell;
    protected final MazeGame game;

    private final Set<Cell>   exploredCells = new HashSet<>();
    private final Deque<Cell> currentPath   = new ArrayDeque<>();

    /** The cell we successfully moved FROM on the previous step (null at start). */
    private Cell prevCell = null;

    /**
     * 30 % random-wander chance, but only triggered at genuine junctions
     * (≥ 2 open neighbours that are NOT the cell we came from).
     */
    private static final double RANDOM_MOVE_CHANCE = 0.30;
    private static final Random RNG = new Random();

    public Bunny(String name, Cell startCell, MazeGame game) {
        super(name);
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

    /** Mark this bunny as resolved (caught or escaped). Called by MazeGame only. */
    void setResolved() { this.resolved = true; }

    /** Returns the current cell this bunny occupies. */
    public Cell getCurrentCell() { return currentCell; }

    // ---------------------------------------------------------------

    @Override
    public void run() {
        System.out.println("[" + getName() + "] starts at (" +
                currentCell.getRow() + "," + currentCell.getCol() + ")");

        while (!game.isGameOver() && !resolved) {
            Cell next = nextTarget();

            if (next != null) {
                if (game.tryMoveBunny(this, next)) {
                    prevCell = currentCell;
                    currentCell = next;
                    exploredCells.add(next);
                    currentPath.poll();
                    // Scan along all straight corridors from new position (line-of-sight)
                    scanStraightLines();
                    // If we just spotted the exit, abandon the current plan so
                    // nextTarget() will immediately route toward it.
                    if (exploredCells.contains(game.getExitCell())) {
                        currentPath.clear();
                    }
                }
            }

            if (game.isGameOver() || resolved) break;

            try {
                waitForNextMove();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[" + getName() + "] stopped.");
        if (game.isGameOver()) {
            game.announceResult();
        }
    }

    /** Number of cells this bunny has explored so far. */
    public int getExploredCount() { return exploredCells.size(); }

    /** True once this bunny has spotted the exit (line-of-sight or stepped on it). */
    public boolean hasSpottedExit() { return exploredCells.contains(game.getExitCell()); }

    // ---------------------------------------------------------------

    /**
     * Scans all four straight corridors from the current cell (true line-of-sight).
     * Every cell visible through open passages until a wall is marked as explored.
     * This lets the bunny spot the exit across any open corridor and route to it.
     */
    private void scanStraightLines() {
        List<List<Cell>> grid = game.getGrid();
        int rows = grid.size(), cols = grid.get(0).size();

        for (Wall dir : Wall.values()) {
            int r = currentCell.getRow();
            int c = currentCell.getCol();
            while (true) {
                Cell here = grid.get(r).get(c);
                if (here.hasWall(dir)) break;          // wall blocks line-of-sight
                r += dir.rowDelta();
                c += dir.colDelta();
                if (r < 0 || r >= rows || c < 0 || c >= cols) break;
                exploredCells.add(grid.get(r).get(c));
            }
        }
    }

    /**
     * Returns the next cell to move toward.
     *
     * Priority:
     *   0. Escape instantly if the exit is an open immediate neighbour.
     *   1. Flee from any robot visible in a straight corridor (line-of-sight).
     *   2. If the exit has been discovered, BFS toward it (dodging siblings).
     *   3. 30 % random wander — but ONLY at a genuine junction (≥ 2 open
     *      neighbours that are not the cell we came from).
     *   4. BFS toward the nearest unvisited cell, routing around siblings.
     */
    private Cell nextTarget() {
        Cell exit = game.getExitCell();
        Set<Cell> others = game.getOtherBunnyPositions(this);

        // Priority 0: escape if exit is one step away
        if (exit != null && isOpenNeighbour(exit) && !others.contains(exit)) {
            return exit;
        }

        // Priority 1: flee from any robot visible in a straight corridor
        Cell flee = fleeCellFromRobots();
        if (flee != null) {
            currentPath.clear();
            return flee;
        }

        // Priority 2: known exit — BFS toward it, dodging siblings
        if (exploredCells.contains(exit)) {
            if (!currentPath.isEmpty() && others.contains(currentPath.peek())) {
                currentPath.clear();
            }
            if (currentPath.isEmpty()) {
                List<Cell> toExit = BFSExplorer.findPathInExplored(
                        currentCell, exit, exploredCells, game.getGrid());
                if (!toExit.isEmpty()) currentPath.addAll(toExit);
            }
            return currentPath.isEmpty() ? null : currentPath.peek();
        }

        // If the planned next step is now blocked by a sibling, replan
        if (!currentPath.isEmpty() && others.contains(currentPath.peek())) {
            currentPath.clear();
        }

        // Priority 3: random wander — only at a genuine junction
        List<Cell> forward = forwardOptions(others);
        if (forward.size() >= 2 && RNG.nextDouble() < RANDOM_MOVE_CHANCE) {
            currentPath.clear();
            return forward.get(RNG.nextInt(forward.size()));
        }

        // Priority 4: BFS toward nearest unvisited
        if (!currentPath.isEmpty()) return currentPath.peek();

        List<Cell> path = BFSExplorer.findNearestUnvisited(
                currentCell, exploredCells, others, game.getGrid());
        if (path.isEmpty()) {
            path = BFSExplorer.findNearestUnvisited(
                    currentCell, exploredCells, game.getGrid());
        }
        if (!path.isEmpty()) {
            currentPath.addAll(path);
            return currentPath.peek();
        }

        return null;
    }

    /**
     * Returns all open neighbours of the current cell that are:
     *   (a) not where we just came from ({@link #prevCell}), and
     *   (b) not occupied by a sibling bunny ({@code exclude}).
     * Used to detect genuine junctions (size ≥ 2 → real choice exists).
     */
    private List<Cell> forwardOptions(Set<Cell> exclude) {
        List<List<Cell>> grid = game.getGrid();
        int rows = grid.size(), cols = grid.get(0).size();
        List<Cell> options = new ArrayList<>();
        for (Wall dir : Wall.values()) {
            if (currentCell.hasWall(dir)) continue;
            int nr = currentCell.getRow() + dir.rowDelta();
            int nc = currentCell.getCol() + dir.colDelta();
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
            Cell neighbour = grid.get(nr).get(nc);
            if (neighbour.equals(prevCell)) continue;   // don't count backtrack
            if (!exclude.contains(neighbour)) options.add(neighbour);
        }
        return options;
    }

    /**
     * Returns true if {@code target} is reachable in exactly one step from
     * the current cell (open passage, no wall).
     */
    private boolean isOpenNeighbour(Cell target) {
        List<List<Cell>> grid = game.getGrid();
        int rows = grid.size(), cols = grid.get(0).size();
        for (Wall dir : Wall.values()) {
            if (currentCell.hasWall(dir)) continue;
            int nr = currentCell.getRow() + dir.rowDelta();
            int nc = currentCell.getCol() + dir.colDelta();
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                    && grid.get(nr).get(nc).equals(target)) return true;
        }
        return false;
    }

    /**
     * Scans the four straight corridors from the current cell for robots.
     * Returns the best cell to flee to (away from the nearest visible robot),
     * or {@code null} if no robot is in line-of-sight.
     *
     * Flee priority: opposite direction → perpendicular directions.
     * Moving toward the robot is never chosen.
     */
    private Cell fleeCellFromRobots() {
        List<List<Cell>> grid = game.getGrid();
        int rows = grid.size(), cols = grid.get(0).size();
        Map<Cell, Robot> robotMap = game.getRobotPositions();
        Set<Cell> robotCells = robotMap.keySet();
        Set<Cell> others = game.getOtherBunnyPositions(this);

        Wall threatDir = null;
        int  threatDist = Integer.MAX_VALUE;

        for (Wall dir : Wall.values()) {
            int r = currentCell.getRow();
            int c = currentCell.getCol();
            int dist = 0;
            while (true) {
                if (grid.get(r).get(c).hasWall(dir)) break;  // wall blocks LOS
                r += dir.rowDelta();
                c += dir.colDelta();
                if (r < 0 || r >= rows || c < 0 || c >= cols) break;
                dist++;
                if (robotCells.contains(grid.get(r).get(c))) {
                    if (dist < threatDist) {
                        threatDist = dist;
                        threatDir  = dir;
                    }
                    break;  // don't look past the robot
                }
            }
        }

        if (threatDir == null) return null;  // no robot visible

        // Build flee preference: opposite first, then perpendiculars
        Wall away = threatDir.opposite();
        List<Wall> preference = new ArrayList<>();
        preference.add(away);
        for (Wall w : Wall.values()) {
            if (w != away && w != threatDir) preference.add(w);
        }

        for (Wall w : preference) {
            if (currentCell.hasWall(w)) continue;
            int nr = currentCell.getRow() + w.rowDelta();
            int nc = currentCell.getCol() + w.colDelta();
            if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
            Cell candidate = grid.get(nr).get(nc);
            if (!others.contains(candidate)) return candidate;
        }
        return null;  // all flee directions blocked
    }

    /**
     * Sleeps until the configured delay has elapsed.
     * Respects {@link #paused}: while paused the deadline is continuously
     * reset so the full delay runs after resuming.
     */
    private void waitForNextMove() throws InterruptedException {
        long deadline = System.currentTimeMillis() + moveDelayMs;
        while (!game.isGameOver() && !resolved) {
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
