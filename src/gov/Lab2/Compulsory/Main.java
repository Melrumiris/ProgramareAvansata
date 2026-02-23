package gov.Lab2.Compulsory;

import gov.Lab2.Location;
import gov.Lab2.Road;

public class Main {
    public static void main(String[] args) {
        Location[] locations = new Location[10];
        locations[0] = new Location(1, 2, "CityA", Location.Type.CITY);
        locations[1] = new Location(3, 4, "VillageB", Location.Type.VILLAGE);
        locations[2] = new Location(5, 6, "AirportC", Location.Type.AIRPORT);
        locations[3] = new Location(7, 8, "PortD", Location.Type.PORT);
        locations[4] = new Location(9, 10, "GasStationE", Location.Type.GAS_STATION);
        locations[5] = new Location(11, 12, "RestaurantF", Location.Type.RESTAURANT);
        locations[6] = new Location(13, 14, "HospitalG", Location.Type.HOSPITAL);
        locations[7] = new Location(15, 16, "SchoolH", Location.Type.SCHOOL);
        locations[8] = new Location(17, 18, "ParkI", Location.Type.PARK);
        locations[9] = new Location(19, 20, "MuseumJ", Location.Type.MUSEUM);

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
