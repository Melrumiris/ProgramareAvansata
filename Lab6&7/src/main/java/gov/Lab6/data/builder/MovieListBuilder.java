package gov.Lab6.data.builder;

import gov.Lab6.data.MovieData;
import gov.Lab6.data.MovieListData;
import gov.Lab6.exception.NullDataException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MovieListBuilder extends DataBuilder<MovieListData> {

    private String name;
    private Timestamp createdAt;
    private List<MovieData> movies = new ArrayList<>();

    public String getName() {
        return name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public List<MovieData> getMovies() {
        return movies;
    }

    public MovieListBuilder setID(int id) {
        super.setID(id);
        return this;
    }

    public MovieListBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MovieListBuilder setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public MovieListBuilder setMovies(List<MovieData> movies) {
        this.movies = movies != null ? movies : new ArrayList<>();
        return this;
    }

    public MovieListBuilder addMovie(MovieData movie) {
        this.movies.add(movie);
        return this;
    }

    public MovieListBuilder() {}

    @Override
    public MovieListData build() throws NullDataException {
        if (getID() == 0) throw new NullDataException("ID must be set");
        if (name == null || name.isBlank()) throw new NullDataException("Name must be set");
        if (createdAt == null) createdAt = Timestamp.from(Instant.now());
        return new MovieListData(this);
    }
}
