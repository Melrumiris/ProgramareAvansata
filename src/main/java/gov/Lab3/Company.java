package gov.Lab3;

import java.util.HashMap;

/**
 * An organisational entity (company, institution, or group) in the social network.
 * <p>
 * {@code Company} is the {@link Profile} implementation for non-human nodes.
 * Companies can be linked to {@link Person} profiles via employment or partnership
 * relationships, and to other companies via partner relationships.
 * </p>
 * <p>
 * Equality and hashing are based solely on the company's {@code name}.
 * </p>
 */
public class Company implements Profile {
    /**
     * Unique numeric identifier.
     */
    long ID;
    /**
     * Human-readable display name.
     */
    String name;
    /**
     * Adjacency map: neighbour profile → relationship descriptor.
     */
    HashMap<Profile, Relationship> relations;

    /**
     * Constructs a new {@code Company} with the given ID and name.
     * The relations map starts empty.
     *
     * @param ID   the unique numeric identifier
     * @param name the display name; must not be {@code null}
     */
    public Company(long ID, String name) {
        this.ID = ID;
        this.name = name;
        relations = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a hash code derived from the company's {@code ID}.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return (int) ID;
    }

    /**
     * Two profiles are equal when they share the same {@code name}.
     *
     * @param o the object to compare with
     * @return {@code true} if {@code o} is a {@link Profile} with the same name
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Profile) {
            return this.name.equals(((Profile) o).getName());
        }
        return false;
    }

    /**
     * Compares this company to another profile alphabetically by name.
     *
     * @param o the profile to compare to
     * @return negative, zero, or positive integer
     */
    @Override
    public int compareTo(Profile o) {
        return name.compareTo(o.getName());
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public Company setName(String name) {
        this.name = name;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public long getID() {
        return ID;
    }

    /** {@inheritDoc} */
    @Override
    public Company setID(long ID) {
        this.ID = ID;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Company addRelation(Profile profile, Relationship relationship) {
        relations.put(profile, relationship);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Company removeRelation(Profile profile) {
        relations.remove(profile);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public HashMap<Profile, Relationship> getRelations() {
        return relations;
    }

    /**
     * Returns the number of direct relationships as this company's importance score.
     *
     * @return degree of this vertex in the social graph
     */
    @Override
    public double getImportance() {
        return relations.size();
    }
}
