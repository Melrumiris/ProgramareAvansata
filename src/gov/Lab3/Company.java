package gov.Lab3;

public class Company implements Profile {
    long ID;
    String name;

    public Company(long ID, String name) {
        this.ID = ID;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int compareTo(Profile o) {
        return (int) (this.ID - o.getID());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Company setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public long getID() {
        return ID;
    }

    @Override
    public Company setID(long ID) {
        this.ID = ID;
        return this;
    }
}
