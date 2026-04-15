package gov.Lab7.service;

import gov.Lab6.dao.CharacterDAO;
import gov.Lab6.dao.MovieDAO;
import gov.Lab6.data.MovieData;
import gov.Lab7.exception.ResourceNotFoundException;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UnrelatedMoviesService {

    private final MovieDAO movieDAO = new MovieDAO();
    private final CharacterDAO characterDAO = new CharacterDAO();

    /** Components at most this large are solved exactly with CP; larger ones use greedy. */
    private static final int CP_COMPONENT_LIMIT = 150;

    public List<MovieData> findUnrelatedMovies(int minSize) {
        List<MovieData> allMovies = movieDAO.getAll();
        if (allMovies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found in the database.");
        }

        // Map DB movie ID → list index for fast lookup
        Map<Integer, Integer> idToIndex = new HashMap<>(allMovies.size() * 2);
        for (int i = 0; i < allMovies.size(); i++) {
            idToIndex.put(allMovies.get(i).getId(), i);
        }

        // Compute conflict pairs via DB join — O(edges) instead of O(n²) in Java
        List<int[]> conflictPairs = characterDAO.getConflictPairs(idToIndex);

        // Build bidirectional adjacency list
        Map<Integer, Set<Integer>> adj = new HashMap<>();
        for (int[] pair : conflictPairs) {
            adj.computeIfAbsent(pair[0], k -> new HashSet<>()).add(pair[1]);
            adj.computeIfAbsent(pair[1], k -> new HashSet<>()).add(pair[0]);
        }

        // Phase 1: isolated movies (no conflicts) are always in the optimal set
        List<MovieData> result = new ArrayList<>();
        boolean[] inConflict = new boolean[allMovies.size()];
        for (int idx : adj.keySet()) {
            inConflict[idx] = true;
        }
        for (int i = 0; i < allMovies.size(); i++) {
            if (!inConflict[i]) {
                result.add(allMovies.get(i));
            }
        }

        // Phase 2: decompose conflicted movies into connected components via BFS
        int[] compId = new int[allMovies.size()];
        Arrays.fill(compId, -1);
        int nextComp = 0;
        for (int start = 0; start < allMovies.size(); start++) {
            if (!inConflict[start] || compId[start] != -1) continue;
            Queue<Integer> queue = new ArrayDeque<>();
            queue.add(start);
            compId[start] = nextComp;
            while (!queue.isEmpty()) {
                int cur = queue.poll();
                for (int nb : adj.getOrDefault(cur, Set.of())) {
                    if (compId[nb] == -1) {
                        compId[nb] = nextComp;
                        queue.add(nb);
                    }
                }
            }
            nextComp++;
        }

        // Group components
        Map<Integer, List<Integer>> components = new HashMap<>();
        for (int i = 0; i < allMovies.size(); i++) {
            if (inConflict[i]) {
                components.computeIfAbsent(compId[i], k -> new ArrayList<>()).add(i);
            }
        }

        // Phase 3: solve each component — CP for small, greedy for large
        for (List<Integer> comp : components.values()) {
            if (comp.size() <= CP_COMPONENT_LIMIT) {
                result.addAll(solveWithCP(allMovies, comp, adj));
            } else {
                result.addAll(solveGreedy(allMovies, comp, adj));
            }
        }

        if (result.size() <= minSize) {
            throw new ResourceNotFoundException(
                    "No set of unrelated movies larger than " + minSize + " could be found.");
        }
        return result;
    }

    private List<MovieData> solveWithCP(List<MovieData> allMovies,
                                         List<Integer> compIndices,
                                         Map<Integer, Set<Integer>> adj) {
        int n = compIndices.size();
        Map<Integer, Integer> globalToLocal = new HashMap<>(n * 2);
        for (int i = 0; i < n; i++) globalToLocal.put(compIndices.get(i), i);

        Model model = new Model("MIS");
        BoolVar[] selected = model.boolVarArray("s", n);

        for (int li = 0; li < n; li++) {
            for (int gj : adj.getOrDefault(compIndices.get(li), Set.of())) {
                Integer lj = globalToLocal.get(gj);
                if (lj != null && lj > li) {
                    model.arithm(selected[li], "+", selected[lj], "<=", 1).post();
                }
            }
        }

        IntVar total = model.intVar("total", 0, n);
        model.sum(selected, "=", total).post();
        model.setObjective(Model.MAXIMIZE, total);

        Solver solver = model.getSolver();
        solver.limitTime("5s");

        List<Integer> best = new ArrayList<>();
        try {
            while (solver.solve()) {
                best.clear();
                for (int i = 0; i < n; i++) {
                    if (selected[i].getValue() == 1) best.add(compIndices.get(i));
                }
            }
        } catch (Exception e) {
            return solveGreedy(allMovies, compIndices, adj);
        }

        if (best.isEmpty()) return solveGreedy(allMovies, compIndices, adj);

        List<MovieData> result = new ArrayList<>(best.size());
        for (int idx : best) result.add(allMovies.get(idx));
        return result;
    }

    /** Greedy MIS: repeatedly pick the node with the fewest remaining conflicts. */
    private List<MovieData> solveGreedy(List<MovieData> allMovies,
                                         List<Integer> compIndices,
                                         Map<Integer, Set<Integer>> adj) {
        List<Integer> sorted = new ArrayList<>(compIndices);
        sorted.sort(Comparator.comparingInt(i -> adj.getOrDefault(i, Set.of()).size()));

        Set<Integer> excluded = new HashSet<>();
        List<MovieData> result = new ArrayList<>();
        for (int idx : sorted) {
            if (!excluded.contains(idx)) {
                result.add(allMovies.get(idx));
                excluded.addAll(adj.getOrDefault(idx, Set.of()));
            }
        }
        return result;
    }
}

