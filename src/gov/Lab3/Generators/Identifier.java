package gov.Lab3.Generators;

import java.util.HashMap;

public class Identifier {
    static HashMap<String, Identifier> instances = new HashMap<>();
    long counter = 0;
    private Identifier(){
    }

    public static Identifier getInstance(){
        instances.putIfAbsent("", new Identifier());
        return instances.get("");
    }

    public static Identifier getInstance(String instanceName){
        instances.putIfAbsent(instanceName, new Identifier());
        return instances.get(instanceName);
    }

    public long getID(){
        return counter++;
    }
}
