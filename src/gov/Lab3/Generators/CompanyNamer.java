package gov.Lab3.Generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CompanyNamer {
    static HashMap<String, CompanyNamer> instances = new HashMap<>();
    static final List<String> nameModel = new LinkedList<>(List.of(
            "TechCorp", "InnovateX", "FutureWorks", "NextGen Solutions", "AlphaTech",
            "BetaSoft", "Gamma Innovations", "Delta Dynamics", "Epsilon Enterprises", "Zeta Systems",
            "Eta Technologies", "Theta Labs", "Iota Software", "Kappa Solutions", "Lambda Inc.",
            "Mu Tech", "Nu Innovations", "Xi Systems", "Omicron Enterprises", "Pi Software",
            "Rho Dynamics", "Sigma Solutions", "Tau Technologies", "Upsilon Labs", "Phi Inc.",
            "Chi Tech", "Psi Innovations", "Omega Systems"
    ));
    List<String> names = new LinkedList<>(nameModel);
    Random random = new Random();
    private CompanyNamer(){
    }

    public static CompanyNamer getInstance(){
        instances.putIfAbsent("", new CompanyNamer());
        return instances.get("");
    }

    public static CompanyNamer getInstance(String instanceName){
        instances.putIfAbsent(instanceName, new CompanyNamer());
        return instances.get(instanceName);
    }

    public boolean hasNames() {
        return !names.isEmpty();
    }

    public String getName(){
        if (names.isEmpty()) {
            names.addAll(nameModel);
        }
        return names.remove(random.nextInt(names.size()));
    }
}
