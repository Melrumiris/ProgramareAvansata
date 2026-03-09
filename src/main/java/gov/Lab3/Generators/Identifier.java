package gov.Lab3.Generators;

import java.util.HashMap;

/**
 * The type Identifier.
 */
public class Identifier {
    /**
     * The Instances.
     */
    static HashMap<String, Identifier> instances = new HashMap<>();
    /**
     * The Counter.
     */
    long counter = 0;
    private Identifier(){
    }

    /**
     * Get instance identifier.
     *
     * @return the identifier
     */
    public static Identifier getInstance(){
        instances.putIfAbsent("", new Identifier());
        return instances.get("");
    }

    /**
     * Get instance identifier.
     *
     * @param instanceName the instance name
     * @return the identifier
     */
    public static Identifier getInstance(String instanceName){
        instances.putIfAbsent(instanceName, new Identifier());
        return instances.get(instanceName);
    }

    /**
     * Get id long.
     *
     * @return the long
     */
    public long getID(){
        return counter++;
    }
}
