package gov.Lab2.Advanced;

import gov.Lab2.*;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    public static Graph generateGraph(int numLocations, int numRoads) {
        Graph graph = new Graph();
        Random random = new Random();
        Location[] locations = new Location[random.nextInt(1000)];
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
            Road road = new Road(location1, location2, random.nextInt(100), random.nextInt(100), Road.Type.values()[random.nextInt(Road.Type.values().length)]);
            if (roads.contains(road))
                continue;
            roads.add(road);
            graph.addRoad(location1, location2, road);
        }
        return graph;
    }
    public static void main(String[] args) {
    }
}
