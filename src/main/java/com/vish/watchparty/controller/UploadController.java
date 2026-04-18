package com.vish.watchparty.controller;

import com.vish.watchparty.model.Movie;
import com.vish.watchparty.repository.MovieRepository;
import com.vish.watchparty.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
public class UploadController {

    private final FileStorageService fileStorageService;
    private final MovieRepository movieRepository;

    public UploadController(FileStorageService fileStorageService, MovieRepository movieRepository) {
        this.fileStorageService = fileStorageService;
        this.movieRepository = movieRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadMovie(
            @RequestParam("groupTitle") String groupTitle,
            @RequestParam("partTitle") String partTitle,
            @RequestParam("partNumber") Integer partNumber,
            @RequestParam("description") String description,
            @RequestParam("poster") MultipartFile poster,
            @RequestParam("video") MultipartFile video
    ) {
        String posterPath = fileStorageService.saveFile(poster);
        String videoPath = fileStorageService.saveFile(video);

        Movie movie = Movie.builder()
                .groupTitle(groupTitle)
                .partTitle(partTitle)
                .partNumber(partNumber)
                .description(description)
                .posterUrl("/uploads/" + posterPath)
                .videoUrl("/uploads/" + videoPath)
                .build();

        movieRepository.save(movie);

        return ResponseEntity.ok(movie);
    }
}