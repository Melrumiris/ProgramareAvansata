package gov.Lab2;

import java.util.HashMap;
import java.util.Set;

public class Graph {
    public HashMap<Vertex<Location>, HashMap<Vertex<Location>, Road>> adjacencyList;
        public Graph() {
            adjacencyList = new HashMap<>();
        }

        public Location[] getLocations() {
            return adjacencyList.keySet().stream().map(Vertex::getData).toArray(Location[]::new);
        }

        public void addLocation(Location location) {
            adjacencyList.put(new Vertex<>(location), new HashMap<>());
        }

        public void addRoad(Location location1, Location location2, Road road) {
            Vertex<Location> vertex1 = new Vertex<>(location1);
            Vertex<Location> vertex2 = new Vertex<>(location2);
            if (!adjacencyList.containsKey(vertex1) || !adjacencyList.containsKey(vertex2))
                throw new IllegalArgumentException("Both locations must be in the graph");
            adjacencyList.get(vertex1).put(vertex2, road);
            adjacencyList.get(vertex2).put(vertex1, road);
        }

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
