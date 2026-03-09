package gov.Lab4;

import java.util.Set;

public class Street {
    private String name;
    private int length;
    private Intersection intersection1;
    private Intersection intersection2;

    public Street(String name, int length, Intersection intersection1, Intersection intersection2) {
        this.name = name;
        this.length = length;
        this.intersection1 = intersection1;
        this.intersection2 = intersection2;
    }
    public String getName() {
        return name;
    }
    public Street setName(String name) {
        this.name = name;
        return this;
    }
    public int getLength() {
        return length;
    }
    public Street setLength(int length) {
        this.length = length;
        return this;
    }
    public Set<Intersection> getIntersections() {
        return Set.of(intersection1, intersection2);
    }
    public Street setIntersections(Intersection intersection1, Intersection intersection2) {
        this.intersection1 = intersection1;
        this.intersection2 = intersection2;
        return this;
    }
}
