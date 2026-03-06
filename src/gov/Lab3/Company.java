package gov.Lab3;

import java.util.HashSet;

public class Company implements Profile {
    long ID;
    String name;
    HashSet<Profile> relations;

    public Company(long ID, String name) {
        this.ID = ID;
        this.name = name;
        relations = new HashSet<>();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int hashCode() {
        return (int) ID;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Profile) {
            return this.name.equals(((Profile) o).getName());
        }
        return false;
    }

    @Override
    public int compareTo(Profile o) {
        return name.compareTo(o.getName());
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

    @Override
    public Company addRelation(Profile profile) {
        relations.add(profile);
        return this;
    }

    @Override
    public Company removeRelation(Profile profile) {
        relations.remove(profile);
        return this;
    }

    @Override
    public HashSet<Profile> getRelations() {
        return relations;
    }

    @Override
    public double getImportance() {
        return relations.size();
    }
}
