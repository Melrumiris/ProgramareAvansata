package gov.Lab6.util;

import gov.Lab6.dao.CharacterDAO;
import gov.Lab6.data.MovieData;
import gov.Lab6.data.MovieListData;
import gov.Lab6.data.builder.MovieListBuilder;
import gov.Lab6.exception.NullDataException;
import org.jgrapht.Graph;
import org.jgrapht.alg.color.LargestDegreeFirstColoring;
import org.jgrapht.alg.interfaces.VertexColoringAlgorithm.Coloring;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MoviePartitioner {

    private final CharacterDAO characterDAO = new CharacterDAO();

    public List<MovieListData> partition(List<MovieData> movies) {
        if (movies.isEmpty()) return List.of();

        Map<Integer, MovieData> byId = new HashMap<>();
        for (MovieData m : movies) byId.put(m.getId(), m);

        Map<Integer, Set<Integer>> actorSets = characterDAO.getMovieActorSets();
        Graph<Integer, DefaultEdge> graph = buildConflictGraph(movies, actorSets);

        Coloring<Integer> coloring = new LargestDegreeFirstColoring<>(graph).getColoring();

        List<List<Integer>> classes = toMutableClasses(coloring);
        rebalance(classes, graph);

        return buildResult(classes, byId);
    }

    private Graph<Integer, DefaultEdge> buildConflictGraph(List<MovieData> movies,
                                                            Map<Integer, Set<Integer>> actorSets) {
        Graph<Integer, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);
        for (MovieData m : movies) graph.addVertex(m.getId());

        List<Integer> ids = movies.stream().map(MovieData::getId).toList();
        for (int i = 0; i < ids.size(); i++) {
            int a = ids.get(i);
            Set<Integer> actorsA = actorSets.getOrDefault(a, Set.of());
            for (int j = i + 1; j < ids.size(); j++) {
                int b = ids.get(j);
                if (!Collections.disjoint(actorsA, actorSets.getOrDefault(b, Set.of()))) {
                    graph.addEdge(a, b);
                }
            }
        }
        return graph;
    }

    private List<List<Integer>> toMutableClasses(Coloring<Integer> coloring) {
        List<List<Integer>> classes = new ArrayList<>();
        for (Set<Integer> colorClass : coloring.getColorClasses()) {
            classes.add(new ArrayList<>(colorClass));
        }
        return classes;
    }

    private void rebalance(List<List<Integer>> classes, Graph<Integer, DefaultEdge> graph) {
        int n = classes.stream().mapToInt(List::size).sum();
        int k = classes.size();
        int targetLow  = n / k;
        int targetHigh = (n + k - 1) / k;

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int from = 0; from < classes.size(); from++) {
                if (classes.get(from).size() <= targetHigh) continue;

                outer:
                for (int mi = 0; mi < classes.get(from).size(); mi++) {
                    int movieId = classes.get(from).get(mi);

                    for (int to = 0; to < classes.size(); to++) {
                        if (to == from || classes.get(to).size() >= targetLow) continue;

                        boolean conflict = classes.get(to).stream()
                                .anyMatch(neighbor -> graph.containsEdge(movieId, neighbor));

                        if (!conflict) {
                            classes.get(from).remove(mi);
                            classes.get(to).add(movieId);
                            changed = true;
                            break outer;
                        }
                    }
                }
            }
        }
    }

    private List<MovieListData> buildResult(List<List<Integer>> classes,
                                             Map<Integer, MovieData> byId) {
        List<MovieListData> result = new ArrayList<>();
        Timestamp now = Timestamp.from(Instant.now());

        for (int i = 0; i < classes.size(); i++) {
            List<MovieData> members = new ArrayList<>();
            for (int movieId : classes.get(i)) {
                MovieData m = byId.get(movieId);
                if (m != null) members.add(m);
            }
            try {
                result.add(new MovieListBuilder()
                        .setID(i + 1)
                        .setName("List " + (i + 1))
                        .setCreatedAt(now)
                        .setMovies(members)
                        .build());
            } catch (NullDataException e) {
                System.err.println("Failed to build MovieListData for class " + i + ": " + e.getMessage());
            }
        }
        return result;
    }
}