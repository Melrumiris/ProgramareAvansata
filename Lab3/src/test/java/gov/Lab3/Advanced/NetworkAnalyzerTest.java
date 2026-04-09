package gov.Lab3.Advanced;

import gov.Lab3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for {@link NetworkAnalyzer}.
 *
 * <p>Test topology legend (each letter is a profile node, each dash is an edge):
 * <ul>
 *   <li><b>Chain</b>:          A - B - C - D</li>
 *   <li><b>Triangle</b>:       A - B - C - A  (fully biconnected)</li>
 *   <li><b>Lollipop</b>:       A - B - C - D - B  (cycle with a tail)</li>
 *   <li><b>Dumbbell</b>:       A-B-C-A  bridge  D-E-F-D  (two triangles joined by B-D bridge)</li>
 *   <li><b>Disconnected</b>:   {A-B-C-A}  {D-E}  (two separate components)</li>
 * </ul>
 */
public class NetworkAnalyzerTest {

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    /** Counter to guarantee unique IDs across all test instances. */
    private long idSeq = 1;

    /** Creates a Person with a unique sequential ID. */
    private Person p(String name) {
        return new Person(idSeq++, name);
    }

    /**
     * Bidirectionally connects two profiles in the given network using a
     * generic FRIENDS relationship (content of the relationship is irrelevant
     * for graph-structure tests).
     */
    private void connect(Network net, Profile a, Profile b) {
        net.addRelation(a, b, new Relationship("Friends"));
    }

    // -----------------------------------------------------------------------
    // Test groups
    // -----------------------------------------------------------------------

    /**
     * The type Articulation point tests.
     */
    @Nested
    @DisplayName("Articulation Points")
    class ArticulationPointTests {

        private NetworkAnalyzer analyzer;

        /**
         * Sets up.
         */
        @BeforeEach
        void setUp() {
            analyzer = new NetworkAnalyzer();
        }

        // -------------------------------------------------------------------

        /**
         * Empty network.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Empty network → no articulation points")
        void emptyNetwork() {
            Network net = new Network();
            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertTrue(aps.isEmpty(), "Empty network should have no articulation points");
        }

        /**
         * Single node.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Single node → no articulation points")
        void singleNode() {
            Network net = new Network();
            Profile a = p("A");
            net.addProfile(a);

            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertTrue(aps.isEmpty(), "Single node cannot be an articulation point");
        }

        /**
         * Two nodes.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Two nodes, one edge → no articulation points")
        void twoNodes() {
            Network net = new Network();
            Profile a = p("A"), b = p("B");
            net.addProfile(a).addProfile(b);
            connect(net, a, b);

            // Removing either node leaves a single isolated vertex (still connected)
            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertTrue(aps.isEmpty(), "A single bridge with two nodes has no articulation point");
        }

        /**
         * Triangle.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Triangle (fully biconnected) → no articulation points")
        void triangle() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C");
            net.addProfile(a).addProfile(b).addProfile(c);
            connect(net, a, b);
            connect(net, b, c);
            connect(net, c, a);

            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertTrue(aps.isEmpty(), "Triangle has no articulation points");
        }

        /**
         * Linear chain.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Linear chain A-B-C-D → B and C are articulation points")
        void linearChain() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C"), d = p("D");
            net.addProfile(a).addProfile(b).addProfile(c).addProfile(d);
            connect(net, a, b);
            connect(net, b, c);
            connect(net, c, d);

            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertEquals(2, aps.size(), "Chain A-B-C-D has exactly 2 articulation points");
            assertTrue(aps.contains(b), "B must be an articulation point");
            assertTrue(aps.contains(c), "C must be an articulation point");
            assertFalse(aps.contains(a), "A (leaf) is not an articulation point");
            assertFalse(aps.contains(d), "D (leaf) is not an articulation point");
        }

        /**
         * Lollipop.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Lollipop: cycle B-C-D-B with tail A-B → B is the only articulation point")
        void lollipop() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C"), d = p("D");
            net.addProfile(a).addProfile(b).addProfile(c).addProfile(d);
            connect(net, a, b); // tail
            connect(net, b, c); // cycle
            connect(net, c, d);
            connect(net, d, b);

            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertEquals(1, aps.size(), "Lollipop graph has exactly 1 articulation point");
            assertTrue(aps.contains(b), "B (junction between tail and cycle) must be the AP");
        }

        /**
         * Dumbbell.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Dumbbell: two triangles A-B-C and D-E-F joined by bridge B-D → B and D are APs")
        void dumbbell() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C");
            Profile d = p("D"), e = p("E"), f = p("F");
            net.addProfile(a).addProfile(b).addProfile(c)
               .addProfile(d).addProfile(e).addProfile(f);

            // Left triangle
            connect(net, a, b);
            connect(net, b, c);
            connect(net, c, a);
            // Bridge
            connect(net, b, d);
            // Right triangle
            connect(net, d, e);
            connect(net, e, f);
            connect(net, f, d);

            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertEquals(2, aps.size(), "Dumbbell has exactly 2 articulation points");
            assertTrue(aps.contains(b), "B (bridge endpoint) must be an AP");
            assertTrue(aps.contains(d), "D (bridge endpoint) must be an AP");
        }

        /**
         * Disconnected two triangles.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Disconnected graph: two separate triangles → no articulation points")
        void disconnectedTwoTriangles() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C");
            Profile d = p("D"), e = p("E"), f = p("F");
            net.addProfile(a).addProfile(b).addProfile(c)
               .addProfile(d).addProfile(e).addProfile(f);

            connect(net, a, b); connect(net, b, c); connect(net, c, a);
            connect(net, d, e); connect(net, e, f); connect(net, f, d);

            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertTrue(aps.isEmpty(), "Two separate triangles have no articulation points");
        }

        /**
         * Disconnected triangle and chain.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Disconnected graph: triangle + chain → C is an AP (in the chain)")
        void disconnectedTriangleAndChain() {
            Network net = new Network();
            // Triangle component
            Profile a = p("A"), b = p("B"), c = p("C");
            // Chain component
            Profile x = p("X"), y = p("Y"), z = p("Z");
            net.addProfile(a).addProfile(b).addProfile(c)
               .addProfile(x).addProfile(y).addProfile(z);

            connect(net, a, b); connect(net, b, c); connect(net, c, a);
            connect(net, x, y); connect(net, y, z);

            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertEquals(1, aps.size(), "Only Y should be an AP");
            assertTrue(aps.contains(y), "Y is the middle of the chain and is the AP");
        }

        /**
         * Star graph.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Star graph: center is the only articulation point (≥3 leaves)")
        void starGraph() {
            Network net = new Network();
            Profile center = p("Center");
            Profile l1 = p("L1"), l2 = p("L2"), l3 = p("L3"), l4 = p("L4");
            net.addProfile(center).addProfile(l1).addProfile(l2)
               .addProfile(l3).addProfile(l4);
            connect(net, center, l1);
            connect(net, center, l2);
            connect(net, center, l3);
            connect(net, center, l4);

            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertEquals(1, aps.size(), "Star center is the only AP");
            assertTrue(aps.contains(center), "Center must be an AP");
        }

        /**
         * Company nodes.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Company nodes are treated exactly like Person nodes")
        void companyNodes() {
            Network net = new Network();
            Profile alice  = p("Alice");
            Profile corp   = new Company(100, "ACME");
            Profile bob    = p("Bob");
            net.addProfile(alice).addProfile(corp).addProfile(bob);
            connect(net, alice, corp);
            connect(net, corp, bob);
            // alice - corp - bob: corp is the single articulation point

            Set<Profile> aps = analyzer.findArticulationPoints(net);
            assertEquals(1, aps.size());
            assertTrue(aps.contains(corp), "ACME (company) must be an AP in the chain");
        }
    }

    // -----------------------------------------------------------------------

    /**
     * The type Biconnected component tests.
     */
    @Nested
    @DisplayName("Biconnected Components")
    class BiconnectedComponentTests {

        private NetworkAnalyzer analyzer;

        /**
         * Sets up.
         */
        @BeforeEach
        void setUp() {
            analyzer = new NetworkAnalyzer();
        }

        // -------------------------------------------------------------------

        /**
         * Empty network.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Empty network → no biconnected components")
        void emptyNetwork() {
            Network net = new Network();
            List<Set<Profile>> bcc = analyzer.findBiconnectedComponents(net);
            assertTrue(bcc.isEmpty(), "No components in empty network");
        }

        /**
         * Single node.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Single isolated node → no biconnected components (no edges)")
        void singleNode() {
            Network net = new Network();
            net.addProfile(p("A"));
            List<Set<Profile>> bcc = analyzer.findBiconnectedComponents(net);
            assertTrue(bcc.isEmpty(), "An isolated node produces no edge-based component");
        }

        /**
         * Triangle.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Triangle → exactly one biconnected component containing all 3 nodes")
        void triangle() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C");
            net.addProfile(a).addProfile(b).addProfile(c);
            connect(net, a, b); connect(net, b, c); connect(net, c, a);

            List<Set<Profile>> bcc = analyzer.findBiconnectedComponents(net);
            assertEquals(1, bcc.size(), "Triangle is a single biconnected component");
            Set<Profile> comp = bcc.get(0);
            assertTrue(comp.contains(a) && comp.contains(b) && comp.contains(c),
                    "The component must contain all three vertices");
        }

        /**
         * Linear chain.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Linear chain A-B-C-D → three components (each bridge edge is its own component)")
        void linearChain() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C"), d = p("D");
            net.addProfile(a).addProfile(b).addProfile(c).addProfile(d);
            connect(net, a, b); connect(net, b, c); connect(net, c, d);

            List<Set<Profile>> bcc = analyzer.findBiconnectedComponents(net);
            assertEquals(3, bcc.size(), "Chain has 3 bridge edges → 3 biconnected components");
        }

        /**
         * Dumbbell.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Dumbbell → three components: left triangle, bridge, right triangle")
        void dumbbell() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C");
            Profile d = p("D"), e = p("E"), f = p("F");
            net.addProfile(a).addProfile(b).addProfile(c)
               .addProfile(d).addProfile(e).addProfile(f);

            connect(net, a, b); connect(net, b, c); connect(net, c, a);
            connect(net, b, d); // bridge
            connect(net, d, e); connect(net, e, f); connect(net, f, d);

            List<Set<Profile>> bcc = analyzer.findBiconnectedComponents(net);
            assertEquals(3, bcc.size(),
                    "Dumbbell must have exactly 3 biconnected components");

            // One component must contain only the bridge endpoints B and D
            boolean hasBridge = bcc.stream()
                    .anyMatch(comp -> comp.size() == 2 && comp.contains(b) && comp.contains(d));
            assertTrue(hasBridge, "The B-D bridge must form its own 2-vertex component");

            // One component must include the left triangle vertices A, B, C
            boolean hasLeftTriangle = bcc.stream()
                    .anyMatch(comp -> comp.contains(a) && comp.contains(b) && comp.contains(c));
            assertTrue(hasLeftTriangle, "Left triangle {A,B,C} must be one component");

            // One component must include the right triangle vertices D, E, F
            boolean hasRightTriangle = bcc.stream()
                    .anyMatch(comp -> comp.contains(d) && comp.contains(e) && comp.contains(f));
            assertTrue(hasRightTriangle, "Right triangle {D,E,F} must be one component");
        }

        /**
         * Lollipop.
         */
        @org.junit.jupiter.api.Test
        @DisplayName("Lollipop A-B with cycle B-C-D-B → two components: {A,B} bridge and {B,C,D} cycle")
        void lollipop() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C"), d = p("D");
            net.addProfile(a).addProfile(b).addProfile(c).addProfile(d);
            connect(net, a, b);
            connect(net, b, c); connect(net, c, d); connect(net, d, b);

            List<Set<Profile>> bcc = analyzer.findBiconnectedComponents(net);
            assertEquals(2, bcc.size(), "Lollipop has 2 biconnected components");

            boolean hasTail = bcc.stream()
                    .anyMatch(comp -> comp.size() == 2 && comp.contains(a) && comp.contains(b));
            assertTrue(hasTail, "The tail {A,B} must be one component");

            boolean hasCycle = bcc.stream()
                    .anyMatch(comp -> comp.contains(b) && comp.contains(c) && comp.contains(d));
            assertTrue(hasCycle, "The cycle {B,C,D} must be one component");
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Disconnected graph: two triangles → two independent biconnected components")
        void disconnectedTwoTriangles() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C");
            Profile d = p("D"), e = p("E"), f = p("F");
            net.addProfile(a).addProfile(b).addProfile(c)
               .addProfile(d).addProfile(e).addProfile(f);

            connect(net, a, b); connect(net, b, c); connect(net, c, a);
            connect(net, d, e); connect(net, e, f); connect(net, f, d);

            List<Set<Profile>> bcc = analyzer.findBiconnectedComponents(net);
            assertEquals(2, bcc.size(),
                    "Two disconnected triangles → exactly 2 biconnected components");
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Fully connected K4 → single biconnected component with 4 nodes")
        void fullyConnectedK4() {
            Network net = new Network();
            Profile a = p("A"), b = p("B"), c = p("C"), d = p("D");
            net.addProfile(a).addProfile(b).addProfile(c).addProfile(d);
            connect(net, a, b); connect(net, a, c); connect(net, a, d);
            connect(net, b, c); connect(net, b, d);
            connect(net, c, d);

            List<Set<Profile>> bcc = analyzer.findBiconnectedComponents(net);
            assertEquals(1, bcc.size(), "K4 is a single biconnected component");
            Set<Profile> comp = bcc.get(0);
            assertTrue(comp.contains(a) && comp.contains(b)
                    && comp.contains(c) && comp.contains(d));
        }

        @org.junit.jupiter.api.Test
        @DisplayName("Analyzer can be reused for multiple independent calls")
        void reuseAnalyzer() {
            NetworkAnalyzer shared = new NetworkAnalyzer();

            Network n1 = new Network();
            Profile a = p("A"), b = p("B"), c = p("C");
            n1.addProfile(a).addProfile(b).addProfile(c);
            connect(n1, a, b); connect(n1, b, c); connect(n1, c, a);

            Network n2 = new Network();
            Profile x = p("X"), y = p("Y"), z = p("Z"), w = p("W");
            n2.addProfile(x).addProfile(y).addProfile(z).addProfile(w);
            connect(n2, x, y); connect(n2, y, z); connect(n2, z, w);

            Set<Profile>       aps1 = shared.findArticulationPoints(n1);
            List<Set<Profile>> bcc1 = shared.findBiconnectedComponents(n1);

            assertTrue(aps1.isEmpty(), "Triangle has no APs");
            assertEquals(1, bcc1.size(), "Triangle is one BCC");

            Set<Profile>       aps2 = shared.findArticulationPoints(n2);
            List<Set<Profile>> bcc2 = shared.findBiconnectedComponents(n2);

            assertEquals(2, aps2.size(), "Chain X-Y-Z-W has 2 APs");
            assertEquals(3, bcc2.size(), "Chain X-Y-Z-W has 3 BCCs");
        }
    }
}

