package org.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Robot extends Thread {

    private static final int MOVE_DELAY_MS = 1500;

    private Cell currentCell;
    private final MazeGame game;
    private final Random rng = new Random();

    public Robot(String name, Cell startCell, MazeGame game) {
        super(name);
        this.currentCell = startCell;
        this.game = game;
        setDaemon(true);
    }

    @Override
    public void run() {
        System.out.println("[" + getName() + "] starts at (" +
                currentCell.getRow() + "," + currentCell.getCol() + ")");

        while (!game.isGameOver()) {
            List<Cell> neighbours = reachableNeighbours();

            if (!neighbours.isEmpty()) {
                Cell next = neighbours.get(rng.nextInt(neighbours.size()));

                // Synchronized move: game ensures no two robots share a cell
                if (game.tryMoveRobot(this, currentCell, next)) {
                    currentCell = next;
                    System.out.println("[" + getName() + "] → (" +
                            currentCell.getRow() + "," + currentCell.getCol() + ")");
                }
            }

            if (game.isGameOver()) break;

            try {
                Thread.sleep(MOVE_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("[" + getName() + "] stopped.");
        game.announceResult();
    }

    // ---------------------------------------------------------------

    private List<Cell> reachableNeighbours() {
        List<Cell> result = new ArrayList<>();
        List<List<Cell>> grid = game.getGrid();
        int rows = grid.size(), cols = grid.get(0).size();

        for (Wall w : Wall.values()) {
            if (currentCell.hasWall(w)) continue;
            int nr = currentCell.getRow() + w.rowDelta();
            int nc = currentCell.getCol() + w.colDelta();
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols)
                result.add(grid.get(nr).get(nc));
        }
        return result;
    }
}
