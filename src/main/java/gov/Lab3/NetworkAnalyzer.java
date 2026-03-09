package gov.Lab3;

import java.util.*;

/**
 * Provides graph-connectivity analysis for a {@link Network}.
 *
 * <h2>Algorithms</h2>
 * <p>
 * Both algorithms run a single DFS pass over the network in <b>O(V + E)</b> time
 * using the classic Tarjan / Hopcroft–Tarjan technique:
 * </p>
 * <ul>
 *   <li>Each vertex is assigned a <em>discovery time</em> ({@code disc}) and a
 *       <em>low value</em> ({@code low}).  The low value of a vertex {@code u} is
 *       the smallest discovery time reachable from the subtree rooted at {@code u}
 *       using at most one back-edge.</li>
 *   <li>A non-root DFS-tree vertex {@code u} is an <em>articulation point</em> if
 *       it has a child {@code v} such that {@code low[v] >= disc[u]} — i.e. no
 *       vertex in the subtree of {@code v} can reach an ancestor of {@code u}
 *       without going through {@code u}.</li>
 *   <li>The DFS root is an articulation point if and only if it has two or more
 *       DFS-tree children.</li>
 *   <li>Edges are pushed onto a stack during DFS; whenever an articulation point
 *       is confirmed the edges accumulated since the last biconnected component
 *       boundary are popped and recorded as one <em>biconnected component</em>.</li>
 * </ul>
 *
 * <h2>Disconnected graphs</h2>
 * <p>
 * The outer loop in {@link #runDFS(Network)} iterates over every unvisited
 * profile, so the algorithms are correct for disconnected networks as well.
 * </p>
 *
 * @see Network
 * @see Profile
 */
public class NetworkAnalyzer {

    // -------------------------------------------------------------------------
    // Internal DFS state
    // -------------------------------------------------------------------------

    /** Maps each profile to its DFS discovery time. */
    private final Map<Profile, Integer> disc = new HashMap<>();

    /** Maps each profile to its low value (smallest disc reachable via back-edge). */
    private final Map<Profile, Integer> low = new HashMap<>();

    /** Tracks whether each profile has been visited during DFS. */
    private final Set<Profile> visited = new HashSet<>();

    /** Maps each profile to its DFS parent. */
    private final Map<Profile, Profile> parent = new HashMap<>();

    /** Monotonically increasing DFS timestamp. */
    private int timer = 0;

    /** Stack of edges used to harvest biconnected components. */
    private final Deque<Profile[]> edgeStack = new ArrayDeque<>();

    /** Collected articulation points (cut vertices). */
    private final Set<Profile> articulationPoints = new HashSet<>();

    /** Collected biconnected components – each is a set of profiles. */
    private final List<Set<Profile>> biconnectedComponents = new ArrayList<>();

    /**
     * Finds all <em>articulation points</em> (cut vertices) in the given network.
     * <p>
     * An articulation point is a profile whose removal disconnects the network
     * (or one of its connected components).
     * </p>
     * <p>
     * The analysis is performed in a single O(V + E) DFS pass.
     * </p>
     *
     * @param network the network to analyse; must not be {@code null}
     * @return an unmodifiable set of profiles that are articulation points;         empty if none exist
     */
    public Set<Profile> findArticulationPoints(Network network) {
        reset();
        runDFS(network);
        return Collections.unmodifiableSet(articulationPoints);
    }

    /**
     * Finds all <em>biconnected components</em> (blocks) of the given network.
     * <p>
     * A biconnected component is a maximal subgraph with no articulation points.
     * Removing any single vertex from a biconnected component keeps it connected.
     * </p>
     * <p>
     * Each component is returned as the set of {@link Profile} vertices it contains.
     * Bridge edges (single-edge components) are represented as two-vertex sets.
     * </p>
     * <p>
     * The analysis shares the same O(V + E) DFS pass as
     * {@link #findArticulationPoints}.
     * </p>
     *
     * @param network the network to analyse; must not be {@code null}
     * @return an unmodifiable list of biconnected components, each represented         as an unmodifiable set of profiles; empty if the network has no edges
     */
    public List<Set<Profile>> findBiconnectedComponents(Network network) {
        reset();
        runDFS(network);
        return Collections.unmodifiableList(biconnectedComponents);
    }


    /**
     * Resets all internal state so the analyzer can be reused for a new network.
     */
    private void reset() {
        disc.clear();
        low.clear();
        visited.clear();
        parent.clear();
        edgeStack.clear();
        articulationPoints.clear();
        biconnectedComponents.clear();
        timer = 0;
    }

    /**
     * Outer DFS driver.  Iterates over all profiles in the network and starts a
     * fresh DFS from each unvisited one, handling disconnected components correctly.
     *
     * @param network the network to traverse
     */
    private void runDFS(Network network) {
        for (Profile profile : network.getProfiles()) {
            if (!visited.contains(profile)) {
                parent.put(profile, null);
                dfs(profile);
            }
        }
    }

    /**
     * Recursive DFS that computes discovery times and low values, identifies
     * articulation points, and collects biconnected components via an edge stack.
     *
     * <p><b>Articulation-point conditions checked here:</b></p>
     * <ol>
     *   <li>Root case: the DFS root is an articulation point if it has ≥ 2
     *       DFS-tree children.</li>
     *   <li>Non-root case: a non-root vertex {@code u} is an articulation point
     *       if it has a child {@code v} with {@code low[v] >= disc[u]}.</li>
     * </ol>
     *
     * @param u the profile currently being explored
     */
    private void dfs(Profile u) {
        visited.add(u);
        disc.put(u, timer);
        low.put(u, timer);
        timer++;

        int childCount = 0; // number of DFS-tree children (used for root detection)

        for (Profile v : u.getRelations().keySet()) {
            if (!visited.contains(v)) {
                // Tree edge: v is a DFS child of u
                childCount++;
                parent.put(v, u);

                // Push this tree edge onto the stack before recursing
                edgeStack.push(new Profile[]{u, v});

                dfs(v);

                // After returning, pull the lowest discovery time up
                low.put(u, Math.min(low.get(u), low.get(v)));

                // ---- Articulation-point check ----
                boolean isRoot = (parent.get(u) == null);

                if (isRoot && childCount >= 2) {
                    // Root with ≥ 2 tree children is an articulation point
                    articulationPoints.add(u);
                }

                if (!isRoot && low.get(v) >= disc.get(u)) {
                    // Non-root: subtree of v cannot reach above u → u is a cut vertex
                    articulationPoints.add(u);

                    // Pop all edges of this biconnected component from the stack
                    harvestComponent(u, v);
                }

            } else if (!v.equals(parent.get(u))) {
                // Back edge (not to direct parent): update low value
                // Push the back-edge onto the stack as well
                if (disc.get(v) < disc.get(u)) {
                    edgeStack.push(new Profile[]{u, v});
                }
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }

        // If u is the DFS root and has exactly one tree child (or zero),
        // the remaining edges on the stack form the last biconnected component
        // of this DFS tree.
        if (parent.get(u) == null && !edgeStack.isEmpty()) {
            harvestRemainingComponent();
        }
    }

    /**
     * Pops edges from the edge stack until the edge {@code (u, v)} is popped
     * (inclusive) and records the collected vertices as one biconnected component.
     *
     * @param u the articulation point (parent side of the triggering tree edge)
     * @param v the child whose low value triggered the articulation-point condition
     */
    private void harvestComponent(Profile u, Profile v) {
        Set<Profile> component = new HashSet<>();
        while (!edgeStack.isEmpty()) {
            Profile[] edge = edgeStack.pop();
            component.add(edge[0]);
            component.add(edge[1]);
            // Stop once we've included the tree edge (u, v)
            if ((edge[0].equals(u) && edge[1].equals(v))
                    || (edge[0].equals(v) && edge[1].equals(u))) {
                break;
            }
        }
        if (!component.isEmpty()) {
            biconnectedComponents.add(Collections.unmodifiableSet(component));
        }
    }

    /**
     * Pops all remaining edges from the edge stack and records them as one
     * biconnected component.  Called when the DFS root finishes to ensure
     * every edge is assigned to exactly one component.
     */
    private void harvestRemainingComponent() {
        Set<Profile> component = new HashSet<>();
        while (!edgeStack.isEmpty()) {
            Profile[] edge = edgeStack.pop();
            component.add(edge[0]);
            component.add(edge[1]);
        }
        if (!component.isEmpty()) {
            biconnectedComponents.add(Collections.unmodifiableSet(component));
        }
    }
}

