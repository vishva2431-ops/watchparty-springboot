package com.vish.watchparty.controller;

import com.vish.watchparty.model.Movie;
import com.vish.watchparty.repository.MovieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{groupTitle}/parts")
    public List<Movie> getParts(@PathVariable String groupTitle) {
        return movieRepository.findByGroupTitleIgnoreCaseOrderByPartNumberAscCreatedAtAsc(groupTitle);
    }

    @PutMapping("/{id}/save")
    public Movie updateMovie(@PathVariable String id, @RequestBody Movie payload) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        movie.setGroupTitle(isBlank(payload.getGroupTitle()) ? movie.getGroupTitle() : payload.getGroupTitle().trim());
        movie.setPartTitle(isBlank(payload.getPartTitle()) ? movie.getPartTitle() : payload.getPartTitle().trim());
        movie.setPartNumber(payload.getPartNumber());
        movie.setDescription(isBlank(payload.getDescription()) ? movie.getDescription() : payload.getDescription().trim());

        if (!isBlank(payload.getPosterUrl())) {
            movie.setPosterUrl(payload.getPosterUrl().trim());
        }

        if (!isBlank(payload.getVideoUrl())) {
            movie.setVideoUrl(payload.getVideoUrl().trim());
        }

        return movieRepository.save(movie);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable String id) {
        Optional<Movie> movieOptional = movieRepository.findById(id);
        if (movieOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }

        movieRepository.deleteById(id);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}