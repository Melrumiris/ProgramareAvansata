package gov.Lab4;

import java.util.HashSet;
import java.util.LinkedList;

public class City {
    public HashSet<Street> streets;
    public LinkedList<Intersection> linkedList;

    public City() {
        streets = new HashSet<>();
        linkedList = new LinkedList<>();
    }

    public City addStreet(Street street) {
        streets.add(street);
        return this;
    }

    public City addIntersection(Intersection intersection) {
        linkedList.add(intersection);
        return this;
    }

    public HashSet<Street> getStreets() {
        return streets;
    }

    public LinkedList<Intersection> getIntersections() {
        return linkedList;
    }

    public City removeStreet(Street street) {
        streets.remove(street);
        return this;
    }

    public City removeIntersection(Intersection intersection) {
        linkedList.remove(intersection);
        return this;
    }


}
