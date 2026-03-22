package gov.Lab5.command;

import gov.Lab5.model.Resource;
import gov.Lab5.util.SetCoverSolver;

import java.util.*;

public class TestPerformanceCommand implements Command {
    @Override
    public void execute(String[] args) {
        System.out.println("Simulating 10,000 resources for performance benchmarking...");
        List<Resource> fakeData = new ArrayList<>();
        Set<String> allTargetConcepts = Set.of("Java", "Algorithms", "AI", "Cloud", "Security", "OOP");
        List<String> conceptList = new ArrayList<>(allTargetConcepts);
        Random rand = new Random();

        for (int i = 0; i < 10000; i++) {
            Set<String> resConcepts = new HashSet<>();
            int numConcepts = rand.nextInt(4) + 1;
            for (int j = 0; j < numConcepts; j++) {
                resConcepts.add(conceptList.get(rand.nextInt(conceptList.size())));
            }
            fakeData.add(new Resource("id" + i, "Simulated Title " + i, "local", 2026, "System", "", resConcepts));
        }

        long start = System.currentTimeMillis();
        List<Resource> result = SetCoverSolver.findSmallestSubset(fakeData, allTargetConcepts);
        long end = System.currentTimeMillis();

        System.out.println("Execution time: " + (end - start) + " ms.");
        System.out.println("Minimal subset required to cover all concepts: " + result.size() + " resources.");
    }

    @Override
    public String getUsage() {
        return "test";
    }
}