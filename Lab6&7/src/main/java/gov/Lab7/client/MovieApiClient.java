package gov.Lab7.client;

import gov.Lab6.data.MovieData;
import gov.Lab6.data.builder.MovieBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class MovieApiClient {

    private final RestClient restClient;

    public MovieApiClient(
            @Value("${app.client.base-url:http://localhost:8080}") String baseUrl,
            @Value("${app.client.token:}") String token) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + token)
                .build();
    }

    public List<MovieData> getAllMovies() {
        return restClient.get()
                .uri("/movies")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public MovieData getMovieById(int id) {
        return restClient.get()
                .uri("/movies/{id}", id)
                .retrieve()
                .body(MovieData.class);
    }

    public MovieData createMovie(MovieBuilder movie) {
        return restClient.post()
                .uri("/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .body(movie)
                .retrieve()
                .body(MovieData.class);
    }

    public MovieData updateMovie(int id, MovieBuilder movie) {
        return restClient.put()
                .uri("/movies/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(movie)
                .retrieve()
                .body(MovieData.class);
    }

    public MovieData patchScore(int id, int score) {
        return restClient.patch()
                .uri("/movies/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("score", score))
                .retrieve()
                .body(MovieData.class);
    }

    public void deleteMovie(int id) {
        restClient.delete()
                .uri("/movies/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}
