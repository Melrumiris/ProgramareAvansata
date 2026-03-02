package gov.Lab2.Advanced;

import gov.Lab2.*;

import java.util.HashSet;
import java.util.Random;
import java.io.*;

/**
 * Entry point for the Advanced Lab 2 exercise.
 * <p>
 * Generates a random graph of locations, then uses {@link Dijkstra} to find and
 * print both the shortest (by distance) and fastest (by travel time) paths between
 * two randomly chosen locations.
 * </p>
 */
public class Main {

    /**
     * Generates a random {@link Graph} and populates the given {@code locations} array in-place.
     * <p>
     * Each slot in {@code locations} is filled with a randomly chosen {@link City},
     * {@link Restaurant}, or {@link Airport} placed at random (x, y) coordinates.
     * A random subset of possible roads is then added between locations, with lengths
     * derived from the Euclidean distance plus a small random offset.
     * </p>
     *
     * @param locations a pre-allocated array whose length determines the number of vertices;                  each element is overwritten with a newly created {@link Location}
     * @return the populated {@link Graph}
     */
    public static Graph generateGraph(Location[] locations) {
        Graph graph = new Graph();
        Random random = new Random();
        for (int i = 0; i < locations.length; i++) {
            int x = random.nextInt(100);
            int y = random.nextInt(100);
            String name = "Location" + i;
            Location location = switch (random.nextInt(3)) {
                case 0 -> new City(x, y, name, random.nextInt(1_000_000));
                case 1 -> new Restaurant(x, y, name, new int[]{random.nextInt(5), random.nextInt(5), random.nextInt(5)});
                case 2 -> new Airport(x, y, name, "www." + name.toLowerCase() + ".com");
                default -> throw new RuntimeException("Impossible case");
            };
            locations[i] = location;
            graph.addLocation(location);
        }
        HashSet<Road> roads = new HashSet<>();
        int maxRoads = random.nextInt(locations.length * (locations.length - 1) / 2);
        for (int i = 0; i < maxRoads; i++){
            Location location1 = locations[random.nextInt(locations.length)];
            Location location2 = locations[random.nextInt(locations.length)];
            if (location1.equals(location2))
                continue;
            Road road = new Road(location1, location2, (int)Math.ceil(Math.sqrt(Math.pow(location1.getX() - location2.getX(), 2) + Math.pow(location1.getY() - location2.getY(), 2)) + random.nextInt(15)), random.nextInt(100) + 20, Road.Type.values()[random.nextInt(Road.Type.values().length)]);
            if (roads.contains(road))
                continue;
            roads.add(road);
            graph.addRoad(location1, location2, road);
        }
        return graph;
    }

    /**
     * Application entry point.
     * <p>
     * Creates a random graph of up to 1 000 locations, selects two random endpoints,
     * and prints both the shortest path and the fastest path found by Dijkstra's algorithm.
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        Random random = new Random();
        var locations = new Location[random.nextInt(1000)];
        Graph graph = generateGraph(locations);
        Location from = locations[random.nextInt(locations.length)];
        Location to = locations[random.nextInt(locations.length)];
        var ShortestPath = Dijkstra.findShortestPath(from, to, graph);
        System.out.print("Shortest path from " + from.getName() + " to " + to.getName() + ": ");
        if (ShortestPath.length == 0) {
            System.out.println("No path found");
        } else {
            for (Location location : ShortestPath) {
                System.out.print(location.getName() + ", ");
            }
        }
        System.out.println();
        var FastestPath = Dijkstra.findFastestPath(from, to, graph);
        System.out.print("Fastest path from " + from.getName() + " to " + to.getName() + ": ");
        if (FastestPath.length == 0) {
            System.out.println("No path found");
        } else {
            for (Location location : FastestPath) {
                System.out.print(location.getName() + ", ");
            }
        }
    }
}
