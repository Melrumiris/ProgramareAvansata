package gov.Lab4;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.alg.util.Triple;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.GraphBuilder;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

public class City {
    final Graph<Intersection, Street> graph;

    public City() {
        graph = new SimpleGraph<>(IntersectionSupplier.getInstance(), StreetSupplier.getInstance(), true);
    }

    public City(Graph<Intersection, Street> graph) {
        this.graph = graph;
    }

    public City addIntersection(){
        graph.addVertex();
        return this;
    }

    public City addIntersection(Intersection intersection) {
        graph.addVertex(intersection);
        return this;
    }

    public City addIntersections(Collection<Intersection> intersections) {
        intersections.forEach(graph::addVertex);
        return this;
    }

    public City addStreet(Intersection from, Intersection to, Street street) {
        if (from != to)
            graph.addEdge(from, to, street);
        return this;
    }

    public City addStreet(Intersection from, Intersection to){
        if (from != to)
            graph.addEdge(from, to);
        return this;
    }

    public Set<Intersection> getIntersections() {
        return graph.vertexSet();
    }

    public Set<Street> getStreets() {
        return graph.edgeSet();
    }

    public Graph<Intersection, Street> getGraph() {
        return graph;
    }

    public boolean isThreeJoined(Street s){
        boolean S = graph.degreeOf(graph.getEdgeSource(s)) >= 3;
        boolean T = graph.degreeOf(graph.getEdgeTarget(s)) >= 3;
        return S || T;
    }

    public List<Iterator<Street>> computeSpanningTrees(int count) {
        List<Iterator<Street>> iteratorList = new ArrayList<>();
        SimpleGraph<Intersection, Street> copy = new GraphBuilder<>(new SimpleGraph<>(IntersectionSupplier.getInstance(), StreetSupplier.getInstance(), true))
                .addGraph(graph)
                .build();
        for (int i = 0; i < count; i++) {
            PrimMinimumSpanningTree<Intersection,Street> algorithm = new PrimMinimumSpanningTree<>(new AsWeightedGraph<>(graph, Street::getLength, false, false));
            iteratorList.add(algorithm.getSpanningTree().iterator());
            if (i == count - 1){
                var bridges = new BiconnectivityInspector<>(copy).getBridges();
                algorithm.getSpanningTree().getEdges().stream().filter(bridges::contains).min(Comparator.comparingDouble(Street::getLength)).ifPresent((Street s) -> graph.removeEdge(s));
            }
        }
        return iteratorList;
    }

    public GraphPath<Intersection, Street> computeTour() {
        return new ChristofidesThreeHalvesApproxMetricTSP<Intersection, Street>().getTour(new AsWeightedGraph<>(graph, Street::getLength, false, false));
    }

    static public City generateCity(int minIntersections, int maxIntersections){
        if (minIntersections > maxIntersections) throw new IllegalArgumentException("max can't be smaller than min");
        City city = new City();
        Supplier<Intersection> interGen = IntersectionSupplier.getInstance();
        ArrayList<Intersection> intersections = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < rand.nextInt(maxIntersections - minIntersections) + minIntersections; i++) {
            Intersection obj = interGen.get();
            city.addIntersection(obj);
            intersections.add(obj);
        }
        for (int i = 0; i < rand.nextInt(intersections.size()*(intersections.size()-1)/2); i++) {
            Intersection from = intersections.get(rand.nextInt(intersections.size()));
            Intersection to = intersections.get(rand.nextInt(intersections.size()));
            city.addStreet(from, to);
        }

        HashSet<Triple<Street, Street, Street>> triangles = city.getTriangleCycles();
        boolean valid = false;
        while (!valid) {
            valid = true;
            for (Triple<Street, Street, Street> t : triangles) {
                Street s1 = t.getFirst();
                Street s2 = t.getSecond();
                Street s3 = t.getThird();
                double l1 = s1.getLength();
                double l2 = s2.getLength();
                double l3 = s3.getLength();
                if (l1 + l2 <= l3) {
                    s3.setLength((l1 + l2) - 1);
                    valid = false;
                } else if (l1 + l3 <= l2) {
                    s2.setLength((l1 + l3) - 1);
                    valid = false;
                } else if (l2 + l3 <= l1) {
                    s1.setLength((l2 + l3) - 1);
                    valid = false;
                }
            }
        }
        return city;
    }

    HashSet<Triple<Street,Street,Street>> getTriangleCycles() {
        HashSet<Triple<Street,Street,Street>> result = new HashSet<>();
        for (Street s1 : graph.edgeSet()) {
            Intersection u = graph.getEdgeSource(s1);
            Intersection v = graph.getEdgeTarget(s1);
            for (Street s2 : graph.edgesOf(u)) {
                if (s2 == s1) continue;
                Intersection w = graph.getEdgeSource(s2).equals(u) ? graph.getEdgeTarget(s2) : graph.getEdgeSource(s2);
                Street s3 = graph.getEdge(v, w);
                if (s3 != null) {
                    List<Street> tri = Arrays.asList(s1, s2, s3);
                    tri.sort(Comparator.comparingInt(System::identityHashCode));
                    result.add(new Triple<>(tri.get(0), tri.get(1), tri.get(2)));
                }
            }
        }
        return result;
    }
}
