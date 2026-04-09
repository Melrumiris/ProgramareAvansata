package gov.Lab3;

import java.util.HashMap;

/**
 * Represents a node in the social network graph.
 * <p>
 * A {@code Profile} can be a {@link Person}, a {@link Company}, or any other
 * entity that participates in the network.  Profiles are connected to each other
 * via {@link Relationship} instances, which are stored in each profile's own
 * adjacency map.
 * </p>
 * <p>
 * Implementations must provide a stable {@code ID} and a human-readable
 * {@code name}.  The {@link #getImportance()} value is used to sort profiles
 * when printing the network.
 * </p>
 */
public interface Profile extends Comparable<Profile> {

    /**
     * Returns the display name of this profile.
     *
     * @return the non-null name string
     */
    String getName();

    /**
     * Sets the display name of this profile.
     *
     * @param name the new name; must not be {@code null}
     * @return this profile instance for method chaining
     */
    Profile setName(String name);

    /**
     * Returns the unique numeric identifier of this profile.
     *
     * @return the profile ID
     */
    long getID();

    /**
     * Sets the unique numeric identifier of this profile.
     *
     * @param ID the new identifier
     * @return this profile instance for method chaining
     */
    Profile setID(long ID);

    /**
     * Returns the map of profiles this profile is directly connected to,
     * together with the {@link Relationship} that describes each connection.
     *
     * @return a mutable adjacency map; never {@code null}
     */
    HashMap<Profile, Relationship> getRelations();

    /**
     * Adds (or replaces) a direct relationship to another profile.
     * <p>
     * Note: the symmetric edge on {@code profile}'s side is <em>not</em> added
     * automatically; use {@link Network#addRelation} to create both directions.
     * </p>
     *
     * @param profile      the neighbour to connect to
     * @param relationship the relationship descriptor
     * @return this profile instance for method chaining
     */
    Profile addRelation(Profile profile, Relationship relationship);

    /**
     * Removes the direct relationship to the given profile, if present.
     * <p>
     * The symmetric edge is <em>not</em> removed automatically.
     * </p>
     *
     * @param profile the neighbour whose edge should be removed
     * @return this profile instance for method chaining
     */
    Profile removeRelation(Profile profile);

    /**
     * Returns a numeric measure of this profile's importance within the network.
     * <p>
     * The default implementations use the number of direct connections (degree)
     * as the importance score.
     * </p>
     *
     * @return a non-negative importance value
     */
    double getImportance();
}