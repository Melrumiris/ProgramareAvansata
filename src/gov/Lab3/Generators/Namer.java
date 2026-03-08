package gov.Lab3.Generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * The type Namer.
 */
public class Namer {
    /**
     * The Instances.
     */
    static HashMap<String, Namer> instances = new HashMap<>();
    /**
     * The Name model.
     */
    static final List<String> nameModel = new LinkedList<>(List.of(
            "Alice", "Bob", "Charlie", "David", "Eve",
            "Frank", "Grace", "Heidi", "Ivan", "Judy",
            "Karl", "Leo", "Mallory", "Nina", "Oscar",
            "Peggy", "Quentin", "Rupert", "Sybil", "Trent",
            "Uma", "Victor", "Walter", "Xavier", "Yvonne",
            "Zara"
    ));
    /**
     * The Names.
     */
    List<String> names = new LinkedList<>(nameModel);
    /**
     * The Random.
     */
    Random random = new Random();
    private Namer(){
    }

    /**
     * Get instance namer.
     *
     * @return the namer
     */
    public static Namer getInstance(){
        instances.putIfAbsent("", new Namer());
        return instances.get("");
    }

    /**
     * Get instance namer.
     *
     * @param instanceName the instance name
     * @return the namer
     */
    public static Namer getInstance(String instanceName){
        instances.putIfAbsent(instanceName, new Namer());
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
