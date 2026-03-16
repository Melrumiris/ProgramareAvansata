package gov.Lab5;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Repository {
    private static final String FILE_PATH = "src/main/resources/Resources.json";
    private final HashMap<String, JSONObject> indexMap = new HashMap<>();

    public Repository() throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(FILE_PATH)));
        if (!content.trim().isEmpty()) {
            JSONArray resources = new JSONArray(new JSONTokener(content));
            resources.forEach(obj -> {
                JSONObject resource = (JSONObject) obj;
                String id = resource.getString("id");
                indexMap.put(id, resource);
            });
        }
    }

    public void save() throws IOException {
        try (FileWriter file = new FileWriter(FILE_PATH)) {
            JSONArray resources = new JSONArray();
            indexMap.values().forEach(resources::put);
            file.write(resources.toString(4));
            file.flush();
        }
    }

    public JSONObject add(String id, String title, String location) {
        JSONObject resource = new JSONObject();
        resource.put("id", id);
        resource.put("title", title);
        resource.put("location", location);
        indexMap.put(id, resource);
        return resource;
    }

    public JSONObject get(String id) {
        return this.indexMap.get(id);
    }

    public Repository openSource(String id) throws IOException {
        return openSource(get(id));
    }

    public Repository openSource(JSONObject obj) throws IOException {
        String link = (String) obj.get("location");
        Desktop desktop = Desktop.getDesktop();
        if (link.contains("https://"))
            desktop.browse(java.net.URI.create(link));
        else
            desktop.open(Paths.get(link).toFile());
        return this;
    }
}
