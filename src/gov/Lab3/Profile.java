package gov.Lab3;

import java.util.HashMap;

public interface Profile extends Comparable<Profile> {
    String getName();

    Profile setName(String name);

    long getID();

    Profile setID(long ID);

    HashMap<Profile, Relationship> getRelations();

    Profile addRelation(Profile profile, Relationship relationship);

    Profile removeRelation(Profile profile);

    double getImportance();
}