package gov.Lab3;

import java.util.Date;
import java.util.HashMap;

/**
 * A human participant in the social network.
 * <p>
 * {@code Person} is the primary {@link Profile} implementation representing an
 * individual.  Each person has a unique numeric {@code ID}, a display {@code name},
 * an optional {@code birthDate}, and a map of direct relationships to other
 * {@link Profile} instances.
 * </p>
 * <p>
 * Equality and hashing are based solely on the person's {@code name}, which
 * means two {@code Person} objects with the same name are considered identical
 * regardless of their {@code ID}.
 * </p>
 *
 * @see Programmer
 * @see Designer
 */
public class Person implements Profile {
    /**
     * Unique numeric identifier.
     */
    long ID;
    /**
     * Human-readable display name.
     */
    String name;
    /**
     * Optional date of birth; may be {@code null}.
     */
    Date birthDate;
    /**
     * Adjacency map: neighbour profile → relationship descriptor.
     */
    HashMap<Profile, Relationship> relations;

    /**
     * Constructs a new {@code Person} with the given ID and name.
     * The birth date is left unset ({@code null}) and the relations map is empty.
     *
     * @param ID   the unique numeric identifier
     * @param name the display name; must not be {@code null}
     */
    public Person(long ID, String name) {
        this.ID = ID;
        this.name = name;
        relations = new HashMap<>();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public Person setID(long ID) {
        this.ID = ID;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public Person setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Returns a hash code derived from the person's {@code ID}.
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

    /** {@inheritDoc} */
    @Override
    public long getID() {
        return ID;
    }

    /**
     * Compares this person to another profile alphabetically by name.
     *
     * @param o the profile to compare to
     * @return negative, zero, or positive as this name is less than, equal to,
     *         or greater than {@code o}'s name
     */
    @Override
    public int compareTo(Profile o) {
        return name.compareTo(o.getName());
    }

    /** {@inheritDoc} */
    @Override
    public HashMap<Profile, Relationship> getRelations() {
        return relations;
    }

    /** {@inheritDoc} */
    @Override
    public Person addRelation(Profile profile, Relationship relationship) {
        relations.put(profile, relationship);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public Person removeRelation(Profile profile) {
        relations.remove(profile);
        return this;
    }

    /**
     * Returns the number of direct relationships as this person's importance score.
     *
     * @return degree of this vertex in the social graph
     */
    @Override
    public double getImportance() {
        return relations.size();
    }

    /**
     * Returns the birth date of this person, or {@code null} if unset.
     *
     * @return the birth date
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Sets the birth date of this person.
     *
     * @param birthDate the birth date to assign; may be {@code null}
     * @return this person instance for method chaining
     */
    public Person setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        return this;
    }
}
