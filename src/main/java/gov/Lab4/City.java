package gov.Lab4;

import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.graph.builder.GraphBuilder;

import java.awt.*;
import java.util.*;
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
}
