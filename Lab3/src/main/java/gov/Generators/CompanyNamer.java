package gov.Lab3.Generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * The type Company namer.
 */
public class CompanyNamer {
    /**
     * The Instances.
     */
    static HashMap<String, CompanyNamer> instances = new HashMap<>();
    /**
     * The Name model.
     */
    static final List<String> nameModel = new LinkedList<>(List.of(
            "TechCorp", "InnovateX", "FutureWorks", "NextGen Solutions", "AlphaTech",
            "BetaSoft", "Gamma Innovations", "Delta Dynamics", "Epsilon Enterprises", "Zeta Systems",
            "Eta Technologies", "Theta Labs", "Iota Software", "Kappa Solutions", "Lambda Inc.",
            "Mu Tech", "Nu Innovations", "Xi Systems", "Omicron Enterprises", "Pi Software",
            "Rho Dynamics", "Sigma Solutions", "Tau Technologies", "Upsilon Labs", "Phi Inc.",
            "Chi Tech", "Psi Innovations", "Omega Systems"
    ));
    /**
     * The Names.
     */
    List<String> names = new LinkedList<>(nameModel);
    /**
     * The Random.
     */
    Random random = new Random();
    private CompanyNamer(){
    }

    /**
     * Get instance company namer.
     *
     * @return the company namer
     */
    public static CompanyNamer getInstance(){
        instances.putIfAbsent("", new CompanyNamer());
        return instances.get("");
    }

    /**
     * Get instance company namer.
     *
     * @param instanceName the instance name
     * @return the company namer
     */
    public static CompanyNamer getInstance(String instanceName){
        instances.putIfAbsent(instanceName, new CompanyNamer());
        return instances.get(instanceName);
    }

    /**
     * Has names boolean.
     *
     * @return the boolean
     */
    public boolean hasNames() {
        return !names.isEmpty();
    }

    /**
     * Get name string.
     *
     * @return the string
     */
    public String getName(){
        if (names.isEmpty()) {
            names.addAll(nameModel);
        }
        return names.remove(random.nextInt(names.size()));
    }
}
