package gov.Lab7.controller;

import gov.Lab6.dao.MovieDAO;
import gov.Lab6.data.MovieData;
import gov.Lab6.data.builder.MovieBuilder;
import gov.Lab6.exception.NullDataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/movies")
public class Controller {
    private final MovieDAO movies = new MovieDAO();
    @GetMapping
    public List<MovieData> getAllMovies() {
        return movies.getAll();
    }

    @PostMapping
    public MovieData createMovie(@RequestBody MovieBuilder movie) {
        movies.add(movie);
        try {
        return movie.build();
        }catch (NullDataException e){
            System.err.println("Failed to build data object:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/{id}")
    public MovieData getMovieById(@PathVariable Integer id) {
        return movies.get(id).orElse(null);
    }
    @PutMapping("/{id}")
    public MovieData updateMovie(@PathVariable Integer id, @RequestBody MovieBuilder movieDetails) {
        movieDetails.setID(id);
        try {
            var entity = movieDetails.build();
            movies.update(entity);
            return entity;
        }catch (NullDataException e) {
            System.err.println("Failed to build data object:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Integer id) {
        movies.remove(movies.get(id).orElseThrow(() -> new IllegalArgumentException("Movie not found with id: " + id)));
    }

}
