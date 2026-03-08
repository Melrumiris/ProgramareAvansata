package gov.Lab3;

import java.util.HashSet;

/**
 * A social network modelled as an undirected graph.
 * <p>
 * Vertices are {@link Profile} instances (persons or companies) and edges are
 * {@link Relationship} instances stored inside each profile's adjacency map.
 * Every call to {@link #addRelation} adds the edge in <em>both</em> directions,
 * keeping the graph undirected.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 *   Network net = new Network();
 *   Profile alice = new Person(1, "Alice");
 *   Profile bob   = new Person(2, "Bob");
 *   net.addProfile(alice).addProfile(bob);
 *   net.addRelation(alice, bob, new Relationship("Friends: 2 years"));
 *   System.out.println(net);
 * }*</pre>
 *
 * @see gov.Lab3.NetworkAnalyzer
 */
public class Network {
    /**
     * The set of all profiles belonging to this network.
     */
    HashSet<Profile> members;

    /**
     * Constructs an empty network with no profiles or relationships.
     */
    public Network() {
        members = new HashSet<>();
    }

    /**
     * Returns the set of all profiles currently in the network.
     *
     * @return a live view of the members set; modifications affect the network
     */
    public HashSet<Profile> getProfiles() {
        return members;
    }

    /**
     * Adds a profile to the network.
     * <p>
     * If the profile is already a member this is a no-op (set semantics).
     * </p>
     *
     * @param profile the profile to add; must not be {@code null}
     * @return this network for method chaining
     */
    public Network addProfile(Profile profile) {
        members.add(profile);
        return this;
    }

    /**
     * Removes a profile from the network.
     * <p>
     * Note: existing edges <em>from</em> other profiles to this profile are
     * not automatically cleaned up.
     * </p>
     *
     * @param profile the profile to remove
     * @return this network for method chaining
     */
    public Network removeProfile(Profile profile) {
        members.remove(profile);
        return this;
    }

    /**
     * Adds a bidirectional relationship between two profiles already in the network.
     * <p>
     * The same {@code relationship} object is stored on both sides of the edge.
     * Calling this method again for the same pair will overwrite the previous
     * relationship.
     * </p>
     *
     * @param profile1     one endpoint of the relationship
     * @param profile2     the other endpoint
     * @param relationship the descriptor of the relationship
     * @return this network for method chaining
     * @throws IllegalArgumentException if either profile is not a member of this network
     */
    public Network addRelation(Profile profile1, Profile profile2, Relationship relationship) {
        if (members.contains(profile1) && members.contains(profile2)) {
            profile1.addRelation(profile2, relationship);
            profile2.addRelation(profile1, relationship);
        } else {
            throw new IllegalArgumentException("Both profiles must be in the network.");
        }
        return this;
    }

    /**
     * Removes the bidirectional relationship between two profiles.
     *
     * @param profile1 one endpoint
     * @param profile2 the other endpoint
     * @return this network for method chaining
     * @throws IllegalArgumentException if either profile is not a member of this network
     */
    public Network removeRelation(Profile profile1, Profile profile2) {
        if (members.contains(profile1) && members.contains(profile2)) {
            profile1.removeRelation(profile2);
            profile2.removeRelation(profile1);
        } else {
            throw new IllegalArgumentException("Both profiles must be in the network.");
        }
        return this;
    }

    /**
     * Returns a human-readable representation of the network.
     * <p>
     * Profiles are listed in descending order of {@link Profile#getImportance()}.
     * For each profile the names of its direct neighbours are printed on the
     * same line, separated by commas.
     * </p>
     *
     * @return formatted string representation of the whole network
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        members.stream()
                .sorted((p1, p2) -> Double.compare(p2.getImportance(), p1.getImportance()))
                .forEach(profile -> {
                    sb.append(profile.getName()).append(" (")
                      .append(profile.getImportance()).append("):\n\t");
                    for (Profile relation : profile.getRelations().keySet()) {
                        sb.append(relation.getName()).append(", ");
                    }
                    sb.append("\n");
                });
        return sb.toString();
    }
}
