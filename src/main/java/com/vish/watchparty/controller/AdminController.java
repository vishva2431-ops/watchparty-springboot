package com.vish.watchparty.controller;

import com.vish.watchparty.model.Movie;
import com.vish.watchparty.repository.MovieRepository;
import com.vish.watchparty.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Instant;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final MovieRepository movieRepository;
    private final FileStorageService fileStorageService;

    public AdminController(MovieRepository movieRepository, FileStorageService fileStorageService) {
        this.movieRepository = movieRepository;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Movie uploadMovie(
            @RequestParam("groupTitle") String groupTitle,
            @RequestParam(value = "partTitle", required = false) String partTitle,
            @RequestParam(value = "partNumber", required = false) String partNumber,
            @RequestParam("description") String description,
            @RequestParam("poster") MultipartFile poster,
            @RequestParam("video") MultipartFile video
    ) throws IOException {
        if (groupTitle == null || groupTitle.isBlank() || description == null || description.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group title and description are required");
        }
        if (poster == null || poster.isEmpty() || video == null || video.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Poster and video are required");
        }

        Movie movie = new Movie();
        movie.setGroupTitle(groupTitle.trim());
        movie.setPartTitle(partTitle == null || partTitle.isBlank() ? groupTitle.trim() : partTitle.trim());
        movie.setPartNumber(parsePartNumber(partNumber));
        movie.setDescription(description.trim());
        movie.setPosterUrl(fileStorageService.savePoster(poster));
        movie.setVideoUrl(fileStorageService.saveVideo(video));
        movie.setCreatedAt(Instant.now());

        return movieRepository.save(movie);
    }

    private Integer parsePartNumber(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}