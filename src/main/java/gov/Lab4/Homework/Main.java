package gov.Lab4.Homework;

import gov.Lab4.City;
import gov.Lab4.Intersection;
import gov.Lab4.IntersectionSupplier;
import gov.Lab4.Street;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        City city = new City();
        Supplier<Intersection> interGen = IntersectionSupplier.getInstance();
        ArrayList<Intersection> intersections = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < 50; i++) {
            Intersection obj = interGen.get();
            city.addIntersection(obj);
            intersections.add(obj);
        }
        for (int i = 0; i < 100; i++) {
            Intersection from = intersections.get(rand.nextInt(intersections.size()));
            Intersection to = intersections.get(rand.nextInt(intersections.size()));
            city.addStreet(from, to);
        }
        city.getStreets().stream().filter((Street s)-> s.getLength() > 250).filter(city::isThreeJoined).forEach(System.out::println);
    }
}
