package gov.Lab3;

public interface Profile extends Comparable<Profile> {
    String getName();
    Profile setName(String name);
    long getID();
    Profile setID(long ID);
}
