package gov.Lab4.Compulsory;

import gov.Lab4.Intersection;
import gov.Lab4.Street;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        List<Street> streets = new LinkedList<>();
        Set<Intersection> intersections = IntStream.range(0, 10)
                .mapToObj(i -> new Intersection("v" + i))
                .collect(Collectors.toCollection(HashSet::new));
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            Street street = new Street("s" + i, random.nextInt(80), new Intersection("v" + i), new Intersection("v" + ((i + 1) % 10)));
            streets.add(street);
        }
        streets.sort(Comparator.comparingInt(Street::getLength));
        System.out.println("Intersection count:" + intersections.size());
        intersections.add(new Intersection("v1"));
        System.out.println("Intersection count after adding duplicate:" + intersections.size());
    }
}
