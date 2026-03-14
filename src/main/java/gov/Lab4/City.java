package gov.Lab4;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.SpanningTreeAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.alg.spanning.PrimMinimumSpanningTree;
import org.jgrapht.alg.tour.ChristofidesThreeHalvesApproxMetricTSP;
import org.jgrapht.graph.AsWeightedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.builder.GraphBuilder;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

public class City {
    Graph<Intersection, Street> graph;

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

    public List<Iterator<Street>> computeSpanningTrees(int count) {
        List<Iterator<Street>> iteratorList = new ArrayList<>();
        SimpleGraph<Intersection, Street> copy = new GraphBuilder<>(new SimpleGraph<>(IntersectionSupplier.getInstance(), StreetSupplier.getInstance(), true))
                .addGraph(graph)
                .build();
        for (int i = 0; i < count; i++) {
            PrimMinimumSpanningTree<Intersection,Street> algorithm = new PrimMinimumSpanningTree<>(new AsWeightedGraph<Intersection,Street>(graph, Street::getLength, false,false));
            iteratorList.add(algorithm.getSpanningTree().iterator());
            if (i == count - 1){
                algorithm.getSpanningTree().getEdges().stream().min(Comparator.comparingDouble(Street::getLength)).ifPresent((Street s) -> graph.removeEdge(s));
            }
        }

        return iteratorList;
    }

    public GraphPath<Intersection, Street> computeTour(Intersection from, Intersection to) {
        return new ChristofidesThreeHalvesApproxMetricTSP<Intersection,Street>().getTour(new AsWeightedGraph<>(graph, Street::getLength, false,false));
    }

    static public City generateCity(){
        City city = new City();

        return city;
    }
}
