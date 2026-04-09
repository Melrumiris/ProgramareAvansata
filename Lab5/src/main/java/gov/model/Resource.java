package gov.Lab5.model;
import java.util.Set;

public record Resource(
        String id,
        String title,
        String location,
        int year,
        String authors,
        String description,
        Set<String> concepts
) {}