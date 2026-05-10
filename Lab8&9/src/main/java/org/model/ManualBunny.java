package org.model;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A bunny controlled by the player via WASD keys or sidebar buttons.
 * Movement is driven by {@link #offerMove(Cell)} calls from the UI thread
 * rather than by any AI pathfinding.
 */
public class ManualBunny extends Bunny {

    /** Capacity-1 queue: newest pending move replaces any previous unprocessed one. */
    private final LinkedBlockingQueue<Cell> moveQueue = new LinkedBlockingQueue<>(1);

    public ManualBunny(Cell startCell, MazeGame game) {
        super("Bunny-Manual", startCell, game);
    }

    /**
     * Enqueues a move to {@code target}. Any previously queued move that has
     * not yet been consumed is discarded (only the latest input counts).
     * Called from the JavaFX / UI thread.
     */
    public void offerMove(Cell target) {
        moveQueue.clear();
        moveQueue.offer(target);
    }

    @Override
    public void run() {
        System.out.println("[Bunny-Manual] starts at (" +
                currentCell.getRow() + "," + currentCell.getCol() + ")");

        while (!game.isGameOver() && !resolved) {
            long deadline = System.currentTimeMillis() + moveDelayMs;
            Cell next = null;
            try {
                // Poll for the first input during this period; execute it immediately
                while (!game.isGameOver() && !resolved) {
                    if (paused) {
                        Thread.sleep(50);
                        deadline = System.currentTimeMillis() + moveDelayMs;
                        continue;
                    }
                    long remaining = deadline - System.currentTimeMillis();
                    if (remaining <= 0) break;
                    next = moveQueue.poll(Math.min(remaining, 50), TimeUnit.MILLISECONDS);
                    if (next != null) break; // got the one move for this period
                }

                if (next != null && !game.isGameOver() && !resolved) {
                    if (game.tryMoveBunny(this, next)) {
                        currentCell = next;
                    }
                }

                // Cooldown: wait out the rest of the period, discarding further input
                long remaining = deadline - System.currentTimeMillis();
                if (remaining > 0) Thread.sleep(remaining);
                moveQueue.clear(); // discard any key presses buffered during cooldown

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[Bunny-Manual] stopped.");
        if (game.isGameOver()) {
            game.announceResult();
        }
    }
}
