package gov.Lab6.data;

import gov.Lab6.data.builder.MovieListBuilder;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

public class MovieListData extends DataRepresentationModel {

    private final String name;
    private final Timestamp createdAt;
    private final List<MovieData> movies;

    public String getName() {
        return name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public List<MovieData> getMovies() {
        return Collections.unmodifiableList(movies);
    }

    public MovieListData(MovieListBuilder builder) {
        super(builder.getID());
        name = builder.getName();
        createdAt = builder.getCreatedAt();
        movies = List.copyOf(builder.getMovies());
    }
}
