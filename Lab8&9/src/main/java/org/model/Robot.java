package org.model;

import org.ai.BFSExplorer;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Robot extends Thread {

    private volatile long moveDelayMs = 1500;

    private volatile boolean paused = false;

    private Cell currentCell;
    private final MazeGame game;

    // --- BFS exploration state ---
    private final Set<Cell>   exploredCells = new HashSet<>();
    private final Deque<Cell> currentPath   = new ArrayDeque<>();

    public Robot(String name, Cell startCell, MazeGame game) {
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

    // ---------------------------------------------------------------

    @Override
    public void run() {

        while (!game.isGameOver()) {
            Cell next = nextTarget();

            if (next != null) {
                if (game.tryMoveRobot(this, currentCell, next)) {
                    currentCell = next;
                    exploredCells.add(next);
                    currentPath.poll();   // consume this step from the plan

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

    private Cell nextTarget() {
        if (!currentPath.isEmpty()) return currentPath.peek();

        // Try to reach the nearest unexplored cell
        List<Cell> path = BFSExplorer.findNearestUnvisited(
                currentCell, exploredCells, game.getGrid());
        if (!path.isEmpty()) {
            currentPath.addAll(path);
            return currentPath.peek();
        }

        // All reachable cells explored — navigate to exit through known cells only
        List<Cell> toExit = BFSExplorer.findPathInExplored(
                currentCell, game.getExitCell(), exploredCells, game.getGrid());
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
                // Keep advancing the deadline so we wait the full delay after resuming
                deadline = System.currentTimeMillis() + moveDelayMs;
                continue;
            }
            if (System.currentTimeMillis() >= deadline) break;
            Thread.sleep(50);
        }
    }
}

