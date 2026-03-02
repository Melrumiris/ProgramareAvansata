package gov.Lab3;

public class Person implements Profile {
    long ID;
    String name;

    public Person(long ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public Person setID(long ID) {
        this.ID = ID;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Person setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public long getID() {
        return ID;
    }

    @Override
    public int compareTo(Profile o) {
        return (int) (this.ID - o.getID());
    }
}
