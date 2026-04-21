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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/movies")
public class MovieController {

    private final MovieRepository movieRepository;
    private final FileStorageService fileStorageService;

    public MovieController(MovieRepository movieRepository, FileStorageService fileStorageService) {
        this.movieRepository = movieRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<Movie> getAllMovies() {
        return movieRepository.findAllByOrderByCreatedAtDesc();
    }

    @GetMapping("/{groupTitle}/parts")
    public List<Movie> getParts(@PathVariable String groupTitle) {
        return movieRepository.findByGroupTitleIgnoreCaseOrderByPartNumberAscCreatedAtAsc(groupTitle);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Movie updateMovie(
            @PathVariable String id,
            @RequestParam("groupTitle") String groupTitle,
            @RequestParam(value = "partTitle", required = false) String partTitle,
            @RequestParam(value = "partNumber", required = false) String partNumber,
            @RequestParam("description") String description,
            @RequestParam(value = "poster", required = false) MultipartFile poster,
            @RequestParam(value = "video", required = false) MultipartFile video
    ) throws IOException {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found"));

        movie.setGroupTitle(groupTitle == null || groupTitle.isBlank() ? movie.getGroupTitle() : groupTitle.trim());
        movie.setPartTitle(partTitle == null || partTitle.isBlank() ? movie.getPartTitle() : partTitle.trim());
        movie.setPartNumber(parsePartNumber(partNumber));
        movie.setDescription(description == null || description.isBlank() ? movie.getDescription() : description.trim());

        if (poster != null && !poster.isEmpty()) {
            movie.setPosterUrl(fileStorageService.savePoster(poster));
        }

        if (video != null && !video.isEmpty()) {
            movie.setVideoUrl(fileStorageService.saveVideo(video));
        }

        return movieRepository.save(movie);
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable String id) {
        Optional<Movie> movieOptional = movieRepository.findById(id);
        if (movieOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found");
        }

        Movie movie = movieOptional.get();

        extractAndDelete(movie.getVideoUrl(), "video");
        extractAndDelete(movie.getPosterUrl(), "image");

        movieRepository.deleteById(id);
    }

    private void extractAndDelete(String url, String resourceType) {
        if (url == null || url.isBlank()) return;

        try {
            String publicId = extractPublicId(url);
            if (publicId != null && !publicId.isBlank()) {
                fileStorageService.delete(publicId, resourceType);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractPublicId(String url) {
        try {
            String marker;

            if (url.contains("/video/upload/")) {
                marker = "/video/upload/";
            } else if (url.contains("/image/upload/")) {
                marker = "/image/upload/";
            } else {
                return null;
            }

            String part = url.substring(url.indexOf(marker) + marker.length());

            String[] pieces = part.split("/");
            int startIndex = 0;

            if (pieces.length > 0 && pieces[0].matches("v\\d+")) {
                startIndex = 1;
            }

            StringBuilder publicId = new StringBuilder();
            for (int i = startIndex; i < pieces.length; i++) {
                if (i > startIndex) {
                    publicId.append("/");
                }
                publicId.append(pieces[i]);
            }

            String result = publicId.toString();
            int dotIndex = result.lastIndexOf(".");
            if (dotIndex != -1) {
                result = result.substring(0, dotIndex);
            }

            return result;
        } catch (Exception e) {
            return null;
        }
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