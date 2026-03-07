package gov.Lab3.Generators;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Namer {
    static HashMap<String, Namer> instances = new HashMap<>();
    static final List<String> nameModel = new LinkedList<>(List.of(
            "Alice", "Bob", "Charlie", "David", "Eve",
            "Frank", "Grace", "Heidi", "Ivan", "Judy",
            "Karl", "Leo", "Mallory", "Nina", "Oscar",
            "Peggy", "Quentin", "Rupert", "Sybil", "Trent",
            "Uma", "Victor", "Walter", "Xavier", "Yvonne",
            "Zara"
    ));
    List<String> names = new LinkedList<>(nameModel);
    Random random = new Random();
    private Namer(){
    }

    public static Namer getInstance(){
        instances.putIfAbsent("", new Namer());
        return instances.get("");
    }

    public static Namer getInstance(String instanceName){
        instances.putIfAbsent(instanceName, new Namer());
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
