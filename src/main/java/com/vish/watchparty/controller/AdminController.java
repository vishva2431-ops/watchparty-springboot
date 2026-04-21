package com.vish.watchparty.controller;

import com.vish.watchparty.model.Movie;
import com.vish.watchparty.repository.MovieRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final MovieRepository movieRepository;

    public AdminController(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @PostMapping("/save-movie")
    public ResponseEntity<?> saveMovie(@RequestBody Map<String, Object> payload) {
        Movie movie = new Movie();

        movie.setGroupTitle(getString(payload.get("groupTitle")));
        movie.setPartTitle(getString(payload.get("partTitle")));
        movie.setPartNumber(parsePartNumber(payload.get("partNumber")));
        movie.setDescription(getString(payload.get("description")));
        movie.setPosterUrl(getString(payload.get("posterUrl")));
        movie.setVideoUrl(getString(payload.get("videoUrl")));
        movie.setCreatedAt(Instant.now());

        movieRepository.save(movie);
        return ResponseEntity.ok(movie);
    }

    private String getString(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private Integer parsePartNumber(Object value) {
        if (value == null) return null;
        String text = value.toString().trim();
        if (text.isBlank()) return null;

        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}