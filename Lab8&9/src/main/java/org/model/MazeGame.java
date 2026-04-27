package org.model;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MazeGame {

    public enum State { RUNNING, BUNNY_ESCAPED, BUNNY_CAUGHT }

    private final List<List<Cell>> grid;
    private final Cell exitCell;

    private Cell bunnyCell;

    private final Map<Cell, Robot> robotPositions = new LinkedHashMap<>();

    private volatile State state = State.RUNNING;
    private String resultMessage = "";
    private final AtomicBoolean resultPrinted = new AtomicBoolean(false);

    // Construction

    public MazeGame(List<List<Cell>> grid, Cell bunnyStart, Cell exitCell, int numRobots) {
        this.grid     = grid;
        this.exitCell = exitCell;
        this.bunnyCell = bunnyStart;

        // Place robots at distinct random cells (not on bunny start or exit)
        List<Cell> pool = shuffledCells();
        pool.remove(bunnyCell);
        pool.remove(exitCell);

        List<Robot> robots = new ArrayList<>();
        for (int i = 0; i < numRobots && !pool.isEmpty(); i++) {
            Cell start = pool.remove(0);
            Robot robot = new Robot("R" + (i + 1), start, this);
            robotPositions.put(start, robot);
            robots.add(robot);
        }

        this.robots = Collections.unmodifiableList(robots);
        this.bunny  = new Bunny(bunnyCell, this);
    }

    private final List<Robot> robots;
    private final Bunny bunny;

    // Lifecycle

    public void start() {
        System.out.println("=== Game started!  Exit → (" +
                exitCell.getRow() + "," + exitCell.getCol() + ") ===");
        bunny.start();
        robots.forEach(Thread::start);
    }

    // Synchronized move primitives

    public synchronized boolean tryMoveBunny(Cell to) {
        if (state != State.RUNNING) return false;

        bunnyCell = to;

        if (robotPositions.containsKey(to)) {
            state = State.BUNNY_CAUGHT;
            resultMessage = "A robot caught the bunny!";
        } else if (to.equals(exitCell)) {
            state = State.BUNNY_ESCAPED;
            resultMessage = "The bunny escaped!";
        }
        return true;
    }

    public synchronized boolean tryMoveRobot(Robot robot, Cell from, Cell to) {
        if (state != State.RUNNING) return false;
        if (robotPositions.containsKey(to)) return false;   // another robot blocks

        robotPositions.remove(from);
        robotPositions.put(to, robot);

        if (to.equals(bunnyCell)) {
            state = State.BUNNY_CAUGHT;
            resultMessage = robot.getName() + " caught the bunny!";
        }
        return true;
    }

    // Queries

    public synchronized boolean isGameOver()  { return state != State.RUNNING; }
    public synchronized State   getState()    { return state; }
    public List<List<Cell>>     getGrid()     { return grid; }
    public Cell                 getExitCell() { return exitCell; }

    // Result announcement (printed exactly once when the game ends)

    public void announceResult() {
        if (resultPrinted.compareAndSet(false, true)) {
            System.out.println("\n=== GAME OVER === " + resultMessage);
        }
    }

    // Helpers


    private List<Cell> shuffledCells() {
        List<Cell> list = new ArrayList<>();
        for (List<Cell> row : grid) list.addAll(row);
        Collections.shuffle(list);
        return list;
    }
}

