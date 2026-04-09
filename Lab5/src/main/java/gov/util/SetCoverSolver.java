package gov.Lab5.util;

import gov.Lab5.model.Resource;

import java.util.*;

public class SetCoverSolver {
    public static List<Resource> findSmallestSubset(List<Resource> allResources, Set<String> targetConcepts) {
        Set<String> remainingConcepts = new HashSet<>(targetConcepts);
        List<Resource> selectedResources = new ArrayList<>();
        Set<Resource> availableResources = new HashSet<>(allResources);

        while (!remainingConcepts.isEmpty()) {
            Resource bestResource = null;
            Set<String> bestCoverage = new HashSet<>();

            for (Resource res : availableResources) {
                Set<String> coverage = new HashSet<>(res.concepts());
                coverage.retainAll(remainingConcepts);
                if (coverage.size() > bestCoverage.size()) {
                    bestCoverage = coverage;
                    bestResource = res;
                }
            }

            if (bestResource == null) break;

            selectedResources.add(bestResource);
            remainingConcepts.removeAll(bestCoverage);
            availableResources.remove(bestResource);
        }
        return selectedResources;
    }
}