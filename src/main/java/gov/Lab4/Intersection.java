package gov.Lab4;

public class Intersection implements Comparable<Intersection> {
    private String name;

    public Intersection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Intersection setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Intersection that = (Intersection) obj;
        return name.equals(that.name);
    }
    public int compareTo(Intersection o) {
        return this.name.compareTo(o.name);
    }
}
