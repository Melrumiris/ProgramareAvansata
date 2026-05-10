package org.model;

import java.util.Map;


public class GameManager extends Thread {

    private final MazeGame game;
    private final long timeLimitMs;
    private final long reportIntervalMs;

    public GameManager(MazeGame game, long timeLimitMs, long reportIntervalMs) {
        super("GameManager");
        this.game = game;
        this.timeLimitMs = timeLimitMs;
        this.reportIntervalMs = reportIntervalMs;
        setDaemon(true);
    }

    @Override
    public void run() {
        System.out.printf("[GameManager] Started – time limit: %ds, report every: %ds%n",
                timeLimitMs / 1000, reportIntervalMs / 1000);

        while (!game.isGameOver()) {
            try {
                Thread.sleep(reportIntervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            if (game.isGameOver()) break;

            long elapsedMs = game.getElapsedMs();
            int totalCells = game.getGrid().size() * game.getGrid().get(0).size();

            System.out.println();
            System.out.println("[GameManager] ── Status Report ───────────────────────────");
            System.out.printf( "  Elapsed  : %d s / %d s%n", elapsedMs / 1000, timeLimitMs / 1000);
            System.out.printf( "  State    : %s%n", game.getState());

            // Report each active bunny
            Map<Bunny, Cell> bunnyPositions = game.getBunnyPositions();
            if (bunnyPositions.isEmpty()) {
                System.out.println("  🐰 No bunnies active yet");
            } else {
                bunnyPositions.forEach((bunny, cell) ->
                        System.out.printf("  🐰 %-12s : (%d, %d)  explored %d/%d cells%s%n",
                                bunny.getName(), cell.getRow(), cell.getCol(),
                                bunny.getExploredCount(), totalCells,
                                bunny.hasSpottedExit() ? "  ✓ EXIT SPOTTED" : ""));
            }

            Map<Cell, Robot> positions = game.getRobotPositions();
            positions.forEach((cell, robot) ->
                    System.out.printf("  🤖 %-6s : (%d, %d)%n",
                            robot.getName(), cell.getRow(), cell.getCol()));
            System.out.println("[GameManager] ────────────────────────────────────────────────────");

            if (elapsedMs >= timeLimitMs) {
                System.out.println("[GameManager] Time limit exceeded! Stopping game.");
                game.forceStop();
                break;
            }
        }

        System.out.printf("[GameManager] Stopped. Final state: %s (%.1f s elapsed)%n",
                game.getState(), game.getElapsedMs() / 1000.0);
    }
}

