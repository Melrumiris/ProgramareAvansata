package gov.Lab5.repository;

import gov.Lab5.exception.InvalidResourceException;
import gov.Lab5.model.Resource;
import org.json.JSONArray;
import org.json.JSONObject;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Repository {
    private final Path filePath;
    private final Map<String, Resource> resources = new LinkedHashMap<>();

    public Repository(String fileName) {
        this.filePath = Paths.get(fileName);
        try {
            load();
        }catch (InvalidResourceException e){
            System.out.println(e.getMessage());
        }
    }

    private void load() throws InvalidResourceException {
        if (!Files.exists(filePath)) return;
        try {
            String content = Files.readString(filePath);
            JSONArray array = new JSONArray(content);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                Set<String> concepts = new HashSet<>();
                if (obj.has("concepts")) {
                    JSONArray conceptsArray = obj.getJSONArray("concepts");
                    for (int j = 0; j < conceptsArray.length(); j++) {
                        concepts.add(conceptsArray.getString(j));
                    }
                }
                Resource res = new Resource(
                        obj.getString("id"), obj.getString("title"), obj.getString("location"),
                        obj.getInt("year"), obj.getString("authors"), obj.optString("description", ""), concepts
                );
                resources.put(res.id(), res);
            }
        } catch (Exception e) {
            throw new InvalidResourceException("Failed to load catalog: " + e.getMessage(), e);
        }
    }

    public void save() throws InvalidResourceException {
        try {
            JSONArray array = new JSONArray();
            for (Resource res : resources.values()) {
                JSONObject obj = new JSONObject();
                obj.put("id", res.id());
                obj.put("title", res.title());
                obj.put("location", res.location());
                obj.put("year", res.year());
                obj.put("authors", res.authors());
                obj.put("description", res.description());
                obj.put("concepts", new JSONArray(res.concepts()));
                array.put(obj);
            }
            Files.writeString(filePath, array.toString(4));
        } catch (Exception e) {
            throw new InvalidResourceException("Failed to save catalog state.", e);
        }
    }

    public void add(Resource resource) throws InvalidResourceException{
        if (resources.containsKey(resource.id())) {
            throw new InvalidResourceException("Conflict: Resource ID '" + resource.id() + "' already exists.");
        }
        resources.put(resource.id(), resource);
        save();
    }

    public List<Resource> getAll() {
        return new ArrayList<>(resources.values());
    }
}