package gov.Lab2;

import java.util.function.Function;

/**
 * Utility class providing Dijkstra's shortest-path algorithm over a {@link Graph}
 * of {@link Location} vertices connected by {@link Road} edges.
 * <p>
 * All methods are static; this class is not meant to be instantiated.
 * </p>
 */
public class Dijkstra {

    /**
     * Finds the optimal path between two locations using a caller-supplied cost metric.
     * <p>
     * Uses Dijkstra's algorithm with a {@link java.util.PriorityQueue}. When a shorter
     * path to a neighbour is discovered the neighbour is removed and re-inserted into
     * the queue so that the ordering reflects the updated distance.
     * </p>
     *
     * @param startLocation the location to start from
     * @param endLocation   the destination location
     * @param graph         the graph to search
     * @param metric        a function that maps a {@link Road} to its integer cost
     * @return an array of {@link Location}s representing the optimal path from start to end,
     *         in order; returns an empty array if no path exists
     */
    public static Location[] findBestPath(Location startLocation, Location endLocation, Graph graph, Function<Road,Integer> metric){
        java.util.Map<Vertex<Location>, Integer> distances = new java.util.HashMap<>();
        java.util.Map<Vertex<Location>, Vertex<Location>> previous = new java.util.HashMap<>();

        for (Vertex<Location> location : graph.adjacencyList.keySet()) {
            distances.put(location, Integer.MAX_VALUE);
            previous.put(location, null);
        }

        Vertex<Location> startVertex = new Vertex<>(startLocation);

        for(Vertex<Location> v : graph.adjacencyList.keySet()) {
            if(v.equals(startVertex)) {
                startVertex = v;
                break;
            }
        }
        distances.put(startVertex, 0);

        java.util.PriorityQueue<Vertex<Location>> queue = new java.util.PriorityQueue<>(java.util.Comparator.comparingInt(distances::get));

        queue.addAll(graph.adjacencyList.keySet());

        var end = new Vertex<>(endLocation);
        while (!queue.isEmpty()) {
            Vertex<Location> current = queue.poll();

            if (distances.get(current) == Integer.MAX_VALUE) break;

            if (current.equals(end))
                break;

            if (graph.adjacencyList.get(current) == null) continue;

            for (Vertex<Location> neighbor : graph.adjacencyList.get(current).keySet()) {
                int alt = distances.get(current) + metric.apply(graph.adjacencyList.get(current).get(neighbor));
                if (alt < distances.get(neighbor)) {
                    queue.remove(neighbor);
                    distances.put(neighbor, alt);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }
        java.util.List<Location> path = new java.util.ArrayList<>();
        for (Vertex<Location> at = end; at != null; at = previous.get(at))
            path.add(at.getData());
        java.util.Collections.reverse(path);
        return path.toArray(new Location[0]);
    }

    /**
     * Finds the shortest path (by distance in km) between two locations.
     *
     * @param start the starting location
     * @param end   the destination location
     * @param graph the graph to search
     * @return an array of {@link Location}s on the shortest path, or an empty array if unreachable
     */
    public static Location[] findShortestPath(Location start, Location end, Graph graph) {
        return findBestPath(start, end, graph, Road::getLength);
    }

    /**
     * Finds the fastest path (by estimated travel time) between two locations.
     * <p>
     * Travel time for each road is estimated as {@code length * 60 / speedLimit} (minutes).
     * </p>
     *
     * @param start the starting location
     * @param end   the destination location
     * @param graph the graph to search
     * @return an array of {@link Location}s on the fastest path, or an empty array if unreachable
     */
    public static Location[] findFastestPath(Location start, Location end, Graph graph) {
        return findBestPath(start, end, graph, road -> road.getLength() * 60 / road.getSpeedLimit());
    }
}
