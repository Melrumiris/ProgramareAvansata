package gov.Lab4;

public class Street {
    private String name;
    private double length;

    public Street(String name, double length) {
        this.name = name;
        this.length = length;
    }
    public String getName() {
        return name;
    }
    public Street setName(String name) {
        this.name = name;
        return this;
    }
    public double getLength() {
        return length;
    }
    public Street setLength(double length) {
        this.length = length;
        return this;
    }

    @Override
    public String toString() {
        return "Street{" +
                "name='" + name + '\'' +
                ", length=" + length +
                '}';
    }
}
