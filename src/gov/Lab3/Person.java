package gov.Lab3;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class Person implements Profile {
    long ID;
    String name;
    Date birthDate;
    HashMap<Profile, Relationship> relations;

    public Person(long ID, String name) {
        this.ID = ID;
        this.name = name;
        relations = new HashMap<>();
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
    public long getID() {
        return ID;
    }

    @Override
    public int compareTo(Profile o) {
        return name.compareTo(o.getName());
    }

    @Override
    public HashMap<Profile, Relationship> getRelations() {
        return relations;
    }

    @Override
    public Person addRelation(Profile profile, Relationship relationship) {
        relations.put(profile, relationship);
        return this;
    }

    @Override
    public Person removeRelation(Profile profile) {
        relations.remove(profile);
        return this;
    }

    @Override
    public double getImportance() {
        return relations.size();
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Person setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        return this;
    }


}

