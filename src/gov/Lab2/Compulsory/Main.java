package gov.Lab2.Compulsory;

import gov.Lab2.*;

public class Main {
    public static void main(String[] args) {
        Location[] locations = new Location[10];
        locations[0] = new City(1, 2, "City", 100000);
        locations[1] = new City(3, 4, "Village", 2000);
        locations[2] = new Airport(5, 6, "Airport", "www.airportc.com");
        locations[3] = new Airport(7, 8, "CloudAir", "www.cloudd.com");
        locations[4] = new Restaurant(9, 10, "GasBar", new int[]{4, 3, 6});
        locations[5] = new Restaurant(11, 12, "Restaurant", new int[]{5, 4});
        locations[6] = new City(13, 14, "Main", 23456783);
        locations[7] = new City(15, 16, "Tiny", 400);
        locations[8] = new Restaurant(17, 18, "ThePark", new int[]{5});
        locations[9] = new Restaurant(19, 20, "FoodPlace", new int[]{4, 5, 3, 5, 5});

        Road[] roads = new Road[10];
        roads[0] = new Road(locations[0], locations[1], 5, 50, Road.Type.STREET);
        roads[1] = new Road(locations[1], locations[2], 10, 100, Road.Type.HIGHWAY);
        roads[2] = new Road(locations[2], locations[3], 15, 80, Road.Type.AVENUE);
        roads[3] = new Road(locations[3], locations[4], 20, 60, Road.Type.BOULEVARD);
        roads[4] = new Road(locations[4], locations[5], 25, 40, Road.Type.LANE);
        roads[5] = new Road(locations[5], locations[6], 30, 30, Road.Type.ALLEY);
        roads[6] = new Road(locations[6], locations[7], 35, 20, Road.Type.OTHER);
        roads[7] = new Road(locations[7], locations[8], 40, 10, Road.Type.STREET);
        roads[8] = new Road(locations[8], locations[9], 45, 5, Road.Type.HIGHWAY);
        roads[9] = new Road(locations[9], locations[0], 50, 0, Road.Type.AVENUE);
        for (Location location : locations) {
            System.out.println(location);
        }
        for (Road road : roads) {
            System.out.println(road);
        }
    }
}
