package gov.Lab3;

import java.util.HashSet;

public interface Profile extends Comparable<Profile> {
    String getName();

    Profile setName(String name);

    long getID();

    Profile setID(long ID);

    HashSet<Profile> getRelations();

    Profile addRelation(Profile profile);

    Profile removeRelation(Profile profile);

    double getImportance();
}