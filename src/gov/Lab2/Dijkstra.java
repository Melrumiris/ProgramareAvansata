package gov.Lab2;

import java.util.function.Function;

public class Dijkstra {
    public static Location[] findBestPath(Location startLocation, Location endLocation, Graph graph, Function<Road,Integer> metric){
        java.util.Map<Vertex<Location>, Integer> distances = new java.util.HashMap<>();
        java.util.Map<Vertex<Location>, Vertex<Location>> previous = new java.util.HashMap<>();
        java.util.PriorityQueue<Vertex<Location>> queue = new java.util.PriorityQueue<>(java.util.Comparator.comparingInt(distances::get));
        for (Vertex<Location> location : graph.adjacencyList.keySet()) {
            distances.put(location, Integer.MAX_VALUE);
            previous.put(location, null);
            queue.add(location);
        }
        distances.put(new Vertex<>(startLocation), 0);
        var end = new Vertex<>(endLocation);
        while (!queue.isEmpty()) {
            Vertex<Location> current = queue.poll();
            if (current.equals(end))
                break;
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
    public static Location[] findShortestPath(Location start, Location end, Graph graph) {
        return findBestPath(start, end, graph, Road::getLength);
    }
    public static Location[] findFastestPath(Location start, Location end, Graph graph) {
        return findBestPath(start, end, graph, road -> road.getLength() * 60 / road.getSpeedLimit());
    }
}
