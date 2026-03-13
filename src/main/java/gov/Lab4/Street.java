package gov.Lab4;

import java.util.Set;

public class Street {
    private String name;
    private int length;

    public Street(String name, int length) {
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
    public int getLength() {
        return length;
    }
    public Street setLength(int length) {
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
