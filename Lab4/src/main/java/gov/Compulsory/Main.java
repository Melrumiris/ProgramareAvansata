package gov.Lab4.Compulsory;

import gov.Lab4.Intersection;
import gov.Lab4.Street;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        Random random = new Random();

        Set<Intersection> intersections = IntStream.range(0, 10)
                .mapToObj(i -> new Intersection("v" + i))
                .collect(Collectors.toCollection(HashSet::new));

        List<Street> streets = IntStream.range(0, 10)
                .mapToObj(i -> new Street("s" + i, random.nextInt(80) + 20))
                .sorted(Comparator.comparingDouble(Street::getLength)).collect(Collectors.toCollection(LinkedList::new));

        System.out.println("Intersection count:" + intersections.size());
        intersections.add(new Intersection("v1"));
        System.out.println("Intersection count after adding duplicate:" + intersections.size());
    }
}
