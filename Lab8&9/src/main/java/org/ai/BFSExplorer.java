package org.ai;

import org.model.Cell;
import org.model.Wall;

import java.util.*;


public class BFSExplorer {

    private BFSExplorer() {}

    public static List<Cell> findPath(Cell from, Cell to, List<List<Cell>> grid) {
        if (from.equals(to)) return Collections.emptyList();

        int rows = grid.size(), cols = grid.get(0).size();
        Map<Cell, Cell> parent = new HashMap<>();
        Queue<Cell> queue = new ArrayDeque<>();
        queue.add(from);
        parent.put(from, null);

        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            if (cur.equals(to)) {
                return buildPath(parent, to);
            }
            for (Wall w : Wall.values()) {
                if (cur.hasWall(w)) continue;
                int nr = cur.getRow() + w.rowDelta();
                int nc = cur.getCol() + w.colDelta();
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                Cell next = grid.get(nr).get(nc);
                if (!parent.containsKey(next)) {
                    parent.put(next, cur);
                    queue.add(next);
                }
            }
        }
        return Collections.emptyList();
    }

    public static List<Cell> findNearestUnvisited(Cell from, Set<Cell> visited,
                                                   List<List<Cell>> grid) {
        int rows = grid.size(), cols = grid.get(0).size();
        Map<Cell, Cell> parent = new HashMap<>();
        Queue<Cell> queue = new ArrayDeque<>();
        queue.add(from);
        parent.put(from, null);

        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            if (!visited.contains(cur) && !cur.equals(from)) {
                return buildPath(parent, cur);
            }
            for (Wall w : Wall.values()) {
                if (cur.hasWall(w)) continue;
                int nr = cur.getRow() + w.rowDelta();
                int nc = cur.getCol() + w.colDelta();
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                Cell next = grid.get(nr).get(nc);
                if (!parent.containsKey(next)) {
                    parent.put(next, cur);
                    queue.add(next);
                }
            }
        }
        return Collections.emptyList();
    }

    public static List<Cell> findPathInExplored(Cell from, Cell to, Set<Cell> explored,
                                                 List<List<Cell>> grid) {
        if (!explored.contains(to)) return Collections.emptyList();
        if (from.equals(to))        return Collections.emptyList();

        int rows = grid.size(), cols = grid.get(0).size();
        Map<Cell, Cell> parent = new HashMap<>();
        Queue<Cell> queue = new ArrayDeque<>();
        queue.add(from);
        parent.put(from, null);

        while (!queue.isEmpty()) {
            Cell cur = queue.poll();
            if (cur.equals(to)) return buildPath(parent, to);
            for (Wall w : Wall.values()) {
                if (cur.hasWall(w)) continue;
                int nr = cur.getRow() + w.rowDelta();
                int nc = cur.getCol() + w.colDelta();
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                Cell next = grid.get(nr).get(nc);
                if (!explored.contains(next)) continue;   // restrict to explored terrain
                if (!parent.containsKey(next)) {
                    parent.put(next, cur);
                    queue.add(next);
                }
            }
        }
        return Collections.emptyList();
    }

    private static List<Cell> buildPath(Map<Cell, Cell> parent, Cell target) {
        LinkedList<Cell> path = new LinkedList<>();
        Cell cur = target;
        while (cur != null && parent.get(cur) != null) {
            path.addFirst(cur);
            cur = parent.get(cur);
        }
        return path;
    }
}
