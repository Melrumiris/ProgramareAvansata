package org.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class MazeGame {

    public enum State { RUNNING, BUNNY_ESCAPED, BUNNY_CAUGHT, TIMEOUT }

    private final List<List<Cell>> grid;
    private final Cell startCell;
    private final Cell exitCell;
    private final int numRobots;
    private final int numBunnies;
    private final boolean manualMode;

    /** Position of each active (spawned, not yet resolved) bunny. Thread-safe map. */
    private final Map<Bunny, Cell> bunnyPositions = new ConcurrentHashMap<>();

    /** Position of each robot, keyed by cell for O(1) occupancy checks. */
    private final Map<Cell, Robot> robotPositions = new LinkedHashMap<>();

    /** Cells where a robot last spotted a bunny, with the timestamp of the sighting. */
    private final Map<Cell, Long> alertedCells = new ConcurrentHashMap<>();

    private volatile State state = State.RUNNING;
    private String resultMessage = "";
    private final AtomicBoolean resultPrinted = new AtomicBoolean(false);
    private final long startTime = System.currentTimeMillis();

    /** Number of bunnies that have been spawned and not yet resolved. */
    private final AtomicInteger activeBunnies = new AtomicInteger(0);

    private final List<Robot> robots;
    private final List<Bunny> bunnies;
    private final ManualBunny manualBunny;

    private BunnySpawner spawner;

    // Construction ----------------------------------------------------------------

    public MazeGame(List<List<Cell>> grid, Cell bunnyStart, Cell exitCell,
                    int numRobots, int numBunnies, boolean manualMode,
                    RobotMemory sharedMemory) {
        this.grid       = grid;
        this.startCell  = bunnyStart;
        this.exitCell   = exitCell;
        this.numRobots  = numRobots;
        this.numBunnies = numBunnies;
        this.manualMode = manualMode;

        // Spawn robots in reachable cells, excluding start and exit
        List<Cell> pool = new ArrayList<>(bfsReachable(bunnyStart));
        pool.remove(bunnyStart);
        pool.remove(exitCell);
        Collections.shuffle(pool);

        List<Robot> robotList = new ArrayList<>();
        for (int i = 0; i < numRobots && !pool.isEmpty(); i++) {
            Cell start = pool.remove(0);
            Robot robot = new Robot("R" + (i + 1), start, this, sharedMemory);
            robotPositions.put(start, robot);
            robotList.add(robot);
        }
        this.robots = Collections.unmodifiableList(robotList);

        // Create bunnies (not started yet — BunnySpawner starts them with delays)
        ManualBunny mb = null;
        List<Bunny> bunnyList = new ArrayList<>();
        for (int i = 0; i < numBunnies; i++) {
            Bunny b;
            if (i == 0 && manualMode) {
                mb = new ManualBunny(bunnyStart, this);
                b  = mb;
            } else {
                b = new Bunny("Bunny-" + (i + 1), bunnyStart, this);
            }
            bunnyList.add(b);
        }
        this.manualBunny = mb;
        this.bunnies     = Collections.unmodifiableList(bunnyList);
    }

    // Lifecycle -------------------------------------------------------------------

    public void start() {
        System.out.println("=== Game started!  Exit → (" +
                exitCell.getRow() + "," + exitCell.getCol() + ") ===");
        robots.forEach(Thread::start);
        spawner = new BunnySpawner();
        spawner.start();
    }

    public synchronized void forceStop() {
        if (state != State.RUNNING) return;
        state = State.TIMEOUT;
        resultMessage = "Time limit exceeded!";
        if (spawner != null) spawner.interrupt();
        bunnies.forEach(Thread::interrupt);
        robots.forEach(Thread::interrupt);
    }

    // Bunny spawner ---------------------------------------------------------------

    /**
     * Daemon thread that releases bunnies from the entrance one by one,
     * waiting two bunny-move-cycles between each spawn.
     */
    private class BunnySpawner extends Thread {
        BunnySpawner() {
            super("BunnySpawner");
            setDaemon(true);
        }

        @Override
        public void run() {
            for (int i = 0; i < bunnies.size(); i++) {
                if (state != MazeGame.State.RUNNING) break;
                Bunny b = bunnies.get(i);
                synchronized (MazeGame.this) {
                    bunnyPositions.put(b, startCell);
                    activeBunnies.incrementAndGet();
                }
                b.start();
                System.out.println("[BunnySpawner] Spawned " + b.getName());

                // Wait two bunny-move cycles before spawning the next one
                if (i < bunnies.size() - 1) {
                    try {
                        Thread.sleep(2 * Bunny.DEFAULT_DELAY_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }

    // Synchronized move primitives ------------------------------------------------

    public synchronized boolean tryMoveBunny(Bunny b, Cell to) {
        if (state != State.RUNNING) return false;
        if (!bunnyPositions.containsKey(b)) return false; // not yet spawned

        // Block bunny-bunny collisions: another bunny already occupies the target cell
        for (Map.Entry<Bunny, Cell> entry : bunnyPositions.entrySet()) {
            if (entry.getKey() != b && to.equals(entry.getValue())) return false;
        }

        bunnyPositions.put(b, to);

        if (robotPositions.containsKey(to)) {
            resolveBunny(b, false, "A robot caught " + b.getName() + "!");
        } else if (to.equals(exitCell)) {
            resolveBunny(b, true, b.getName() + " escaped!");
        }
        return true;
    }

    public synchronized boolean tryMoveRobot(Robot robot, Cell from, Cell to) {
        if (state != State.RUNNING) return false;
        if (robotPositions.containsKey(to)) return false;   // another robot blocks

        robotPositions.remove(from);
        robotPositions.put(to, robot);

        // Check if the robot landed on any active bunny
        for (Map.Entry<Bunny, Cell> entry : new ArrayList<>(bunnyPositions.entrySet())) {
            if (to.equals(entry.getValue())) {
                resolveBunny(entry.getKey(), false,
                        robot.getName() + " caught " + entry.getKey().getName() + "!");
                break;
            }
        }
        return true;
    }

    // Alert markers ---------------------------------------------------------------

    /** Records that a robot spotted a bunny at {@code cell} right now. */
    public void markBunnySpotted(Cell cell) {
        alertedCells.put(cell, System.currentTimeMillis());
    }

    /** Returns a snapshot of alert cells with their sighting timestamps. */
    public Map<Cell, Long> getAlertedCells() {
        return new HashMap<>(alertedCells);
    }

    // Queries ---------------------------------------------------------------------

    public synchronized boolean      isGameOver()     { return state != State.RUNNING; }

    /**
     * Returns the set of cells currently occupied by all bunnies <em>other than</em>
     * {@code self}.  Used by the bunny AI to route around siblings.
     */
    public Set<Cell> getOtherBunnyPositions(Bunny self) {
        Set<Cell> positions = new java.util.HashSet<>();
        bunnyPositions.forEach((b, cell) -> {
            if (b != self) positions.add(cell);
        });
        return positions;
    }
    public synchronized State        getState()       { return state; }
    public synchronized String        getResultMessage() { return resultMessage; }
    public List<List<Cell>>          getGrid()        { return grid; }
    public Cell                      getExitCell()    { return exitCell; }
    public List<Robot>               getRobots()      { return robots; }
    public List<Bunny>               getBunnies()     { return bunnies; }
    public ManualBunny               getManualBunny() { return manualBunny; }
    public long                      getElapsedMs()   { return System.currentTimeMillis() - startTime; }
    public int                       getNumRobots()   { return numRobots; }
    public int                       getNumBunnies()  { return numBunnies; }
    public boolean                   isManualMode()   { return manualMode; }

    /** Snapshot of all currently active (spawned, not resolved) bunny positions. */
    public Map<Bunny, Cell> getBunnyPositions() {
        return new LinkedHashMap<>(bunnyPositions);
    }

    /** Returns the first active bunny's position, or null if none are active yet. */
    public synchronized Cell getBunnyCell() {
        return bunnyPositions.values().stream().findFirst().orElse(null);
    }

    public synchronized Map<Cell, Robot> getRobotPositions() {
        return new LinkedHashMap<>(robotPositions);
    }

    // Result announcement (printed exactly once when the game ends) ---------------

    public void announceResult() {
        if (resultPrinted.compareAndSet(false, true)) {
            System.out.println("\n=== GAME OVER === " + resultMessage);
        }
    }

    // Helpers ---------------------------------------------------------------------

    /**
     * Resolves a bunny (escaped or caught). Decrements the active counter and
     * ends the game if appropriate:
     *   - Any escape      → game over immediately (BUNNY_ESCAPED).
     *   - Last bunny caught → game over (BUNNY_CAUGHT).
     *   - Otherwise        → game continues for remaining bunnies.
     */
    private void resolveBunny(Bunny b, boolean escaped, String msg) {
        if (bunnyPositions.remove(b) == null) return; // already resolved (guard)
        b.setResolved();
        int remaining = activeBunnies.decrementAndGet();

        if (escaped) {
            state = State.BUNNY_ESCAPED;
            resultMessage = msg;
            if (spawner != null) spawner.interrupt();
            bunnies.forEach(other -> { if (!other.equals(b)) other.interrupt(); });
            robots.forEach(Thread::interrupt);
        } else if (remaining == 0) {
            state = State.BUNNY_CAUGHT;
            resultMessage = msg;
            if (spawner != null) spawner.interrupt();
            robots.forEach(Thread::interrupt);
        }
        // else: more bunnies still active — game continues
    }

    private Set<Cell> bfsReachable(Cell start) {
        Set<Cell> visited = new LinkedHashSet<>();
        Queue<Cell> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        int rows = grid.size(), cols = grid.get(0).size();
        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            for (Wall w : Wall.values()) {
                if (cur.hasWall(w)) continue;
                int nr = cur.getRow() + w.rowDelta();
                int nc = cur.getCol() + w.colDelta();
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                Cell neighbour = grid.get(nr).get(nc);
                if (visited.add(neighbour)) queue.add(neighbour);
            }
        }
        return visited;
    }
}


