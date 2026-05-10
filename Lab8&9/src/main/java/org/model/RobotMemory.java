package org.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Shared memory pool for all robots in a game.
 * Thread-safe: all methods may be called concurrently from robot threads.
 */
public class RobotMemory {

    /** Every cell any robot has ever stepped on or explored. */
    private final Set<Cell> sharedExploredCells = Collections.synchronizedSet(new HashSet<>());

    /** Most recently reported bunny position; null if never spotted. */
    private volatile Cell lastKnownBunnyLocation = null;

    /** Merge a robot's explored cells into the shared pool. */
    public void addAllExplored(Collection<Cell> cells) {
        sharedExploredCells.addAll(cells);
    }

    /** Returns a consistent snapshot of all cells explored by any robot. */
    public Set<Cell> getSharedExploredCellsSnapshot() {
        synchronized (sharedExploredCells) {
            return new HashSet<>(sharedExploredCells);
        }
    }

    /** Called when a robot spots the bunny at {@code cell}. */
    public void reportBunnySighting(Cell cell) {
        this.lastKnownBunnyLocation = cell;
    }

    /** Returns the last cell where a robot saw a bunny, or null if never spotted. */
    public Cell getLastKnownBunnyLocation() {
        return lastKnownBunnyLocation;
    }
}
