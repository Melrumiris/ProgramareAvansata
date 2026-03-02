package gov.Lab2;

import java.util.HashMap;
import java.util.Set;

/**
 * An undirected weighted graph of {@link Location} vertices connected by {@link Road} edges.
 * <p>
 * Internally represented as an adjacency list mapping each {@link Vertex} to its
 * neighbouring vertices and the {@link Road} that connects them.
 * </p>
 */
public class Graph {
    /** The adjacency list storing all vertices and their outgoing edges. */
    public HashMap<Vertex<Location>, HashMap<Vertex<Location>, Road>> adjacencyList;

    /**
     * Constructs an empty graph.
     */
    public Graph() {
        adjacencyList = new HashMap<>();
    }

    /**
     * Adds a location to the graph as an isolated vertex with no edges.
     *
     * @param location the location to add
     */
    public void addLocation(Location location) {
        adjacencyList.put(new Vertex<>(location), new HashMap<>());
    }

    /**
     * Adds a bidirectional road between two already-registered locations.
     *
     * @param location1 the first endpoint
     * @param location2 the second endpoint
     * @param road      the road connecting them
     * @throws IllegalArgumentException if either location is not present in the graph
     */
    public void addRoad(Location location1, Location location2, Road road) {
        Vertex<Location> vertex1 = new Vertex<>(location1);
        Vertex<Location> vertex2 = new Vertex<>(location2);
        if (!adjacencyList.containsKey(vertex1) || !adjacencyList.containsKey(vertex2))
            throw new IllegalArgumentException("Both locations must be in the graph");
        adjacencyList.get(vertex1).put(vertex2, road);
        adjacencyList.get(vertex2).put(vertex1, road);
    }

    /**
     * Checks whether the graph is structurally valid.
     * <p>
     * A graph is valid when:
     * <ul>
     *   <li>No two distinct vertices hold the same location (by value).</li>
     *   <li>Every {@link Road} edge appears at most twice in the adjacency list
     *       (once per direction for an undirected edge).</li>
     * </ul>
     * </p>
     *
     * @return {@code true} if the graph is valid, {@code false} otherwise
     */
    public boolean isValid(){
        HashMap<Road, Integer> edges = new java.util.HashMap<>();
        for (Vertex<Location> first : adjacencyList.keySet()){
            for (Vertex<Location> second : adjacencyList.keySet())
                if (first.getData().equals(second.getData()))
                    return false;
            for (Road edge : adjacencyList.get(first).values()) {
                switch (edges.get(edge)) {
                    case null -> edges.put(edge, 1);
                    case 1 -> edges.put(edge, 2);
                    default -> {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines whether there is a path between two vertices using depth-first search.
     *
     * @param start   the starting vertex
     * @param end     the target vertex
     * @param visited a set used to track already-visited vertices during traversal;
     *                should be empty when called externally
     * @return {@code true} if a path from {@code start} to {@code end} exists
     */
    public boolean hasPath(Vertex<Location> start, Vertex<Location> end, Set<Vertex<Location>> visited) {
        if (start.equals(end))
            return true;
        visited.add(start);
        for (Vertex<Location> neighbor : adjacencyList.get(start).keySet()) {
            if (!visited.contains(neighbor) && hasPath(neighbor, end, visited))
                return true;
        }
        visited.remove(start);
        return false;
    }
}
