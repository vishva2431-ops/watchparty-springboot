package com.vish.watchparty.controller;

import com.vish.watchparty.model.Movie;
import com.vish.watchparty.repository.MovieRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;

    public MovieController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @GetMapping
    public ResponseEntity<?> getGroupedMovies() {
        List<Movie> all = movieRepository.findAll();

        Map<String, Movie> grouped = new LinkedHashMap<>();
        for (Movie movie : all) {
            grouped.putIfAbsent(movie.getGroupTitle(), movie);
        }

        return ResponseEntity.ok(new ArrayList<>(grouped.values()));
    }

    @GetMapping("/{groupTitle}/parts")
    public ResponseEntity<?> getMovieParts(@PathVariable String groupTitle) {
        return ResponseEntity.ok(
                movieRepository.findByGroupTitleIgnoreCaseOrderByPartNumberAsc(groupTitle)
        );
    }
}