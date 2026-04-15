package gov.Lab7.controller;

import gov.Lab6.dao.MovieDAO;
import gov.Lab6.data.MovieData;
import gov.Lab6.data.MovieListData;
import gov.Lab6.data.builder.MovieBuilder;
import gov.Lab6.exception.IllegalDataException;
import gov.Lab6.exception.NullDataException;
import gov.Lab6.util.MoviePartitioner;
import gov.Lab7.exception.ResourceNotFoundException;
import gov.Lab7.service.UnrelatedMoviesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movies")
@SecurityRequirement(name = "bearerAuth")
public class Controller {

    private final MovieDAO movies = new MovieDAO();
    private final UnrelatedMoviesService unrelatedMoviesService;

    public Controller(UnrelatedMoviesService unrelatedMoviesService) {
        this.unrelatedMoviesService = unrelatedMoviesService;
    }

    @GetMapping
    @Operation(summary = "Get all movies")
    public List<MovieData> getAllMovies() {
        return movies.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a movie by ID")
    public ResponseEntity<MovieData> getMovieById(@PathVariable int id) {
        MovieData movie = movies.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        return ResponseEntity.ok(movie);
    }

    @PostMapping
    @Operation(summary = "Add a new movie")
    public ResponseEntity<MovieData> createMovie(@RequestBody MovieBuilder movie) {
        movies.add(movie);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(movie.build());
        } catch (NullDataException e) {
            throw new IllegalArgumentException("Invalid movie data: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace an existing movie")
    public ResponseEntity<MovieData> updateMovie(@PathVariable int id,
                                                  @RequestBody MovieBuilder movieDetails) {
        movies.get(id).orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        movieDetails.setID(id);
        try {
            MovieData entity = movieDetails.build();
            movies.update(entity);
            return ResponseEntity.ok(entity);
        } catch (NullDataException e) {
            throw new IllegalArgumentException("Invalid movie data: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update only the score of a movie")
    public ResponseEntity<MovieData> patchScore(@PathVariable int id,
                                                 @RequestBody ScoreUpdateRequest request) {
        MovieData existing = movies.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        MovieBuilder builder = new MovieBuilder(existing);
        try {
            builder.setScore(request.score());
            MovieData updated = builder.build();
            movies.update(updated);
            return ResponseEntity.ok(updated);
        } catch (IllegalDataException e) {
            throw new IllegalArgumentException("Invalid score: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a movie")
    public ResponseEntity<Void> deleteMovie(@PathVariable int id) {
        MovieData movie = movies.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
        movies.remove(movie);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partition")
    @Operation(summary = "Partition movies into independent sets (no shared actors)")
    public List<MovieListData> partitionMovies() {
        return new MoviePartitioner().partition(movies.getAll());
    }

    @GetMapping("/unrelated")
    @Operation(summary = "Find a maximum set of mutually unrelated movies (CP solver)")
    public ResponseEntity<List<MovieData>> findUnrelatedMovies(
            @RequestParam(defaultValue = "0") int minSize) {
        List<MovieData> result = unrelatedMoviesService.findUnrelatedMovies(minSize);
        return ResponseEntity.ok(result);
    }

    record ScoreUpdateRequest(Integer score) {}
}
