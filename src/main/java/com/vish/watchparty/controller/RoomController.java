package com.vish.watchparty.controller;

import com.vish.watchparty.model.Movie;
import com.vish.watchparty.model.Room;
import com.vish.watchparty.repository.MovieRepository;
import com.vish.watchparty.repository.RoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;

    public RoomController(RoomRepository roomRepository, MovieRepository movieRepository) {
        this.roomRepository = roomRepository;
        this.movieRepository = movieRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody(required = false) Map<String, String> payload) {
        String movieId = null;

        if (payload != null) {
            movieId = payload.get("movieId");
        }

        if (movieId != null && !movieId.isBlank()) {
            Optional<Movie> movieOptional = movieRepository.findById(movieId);
            if (movieOptional.isEmpty()) {
                return ResponseEntity.badRequest().body("Movie not found");
            }
        }

        String roomCode = generateUniqueRoomCode();

        Room room = Room.builder()
                .roomCode(roomCode)
                .movieId(movieId)
                .currentTime(0.0)
                .playing(false)
                .playbackRate(1.0)
                .currentQuality("AUTO")
                .build();

        roomRepository.save(room);

        return ResponseEntity.ok(room);
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<?> getRoom(@PathVariable String roomCode) {
        return roomRepository.findByRoomCode(roomCode)
                .map(room -> {
                    Movie movie = null;
                    if (room.getMovieId() != null) {
                        movie = movieRepository.findById(room.getMovieId()).orElse(null);
                    }

                    return ResponseEntity.ok(Map.of(
                            "roomCode", room.getRoomCode(),
                            "movie", movie == null ? "" : movie,
                            "currentTime", room.getCurrentTime() == null ? 0.0 : room.getCurrentTime(),
                            "playing", room.getPlaying() == null ? false : room.getPlaying(),
                            "playbackRate", room.getPlaybackRate() == null ? 1.0 : room.getPlaybackRate(),
                            "currentQuality", room.getCurrentQuality() == null ? "AUTO" : room.getCurrentQuality()
                    ));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{roomCode}/movie")
    public ResponseEntity<?> updateRoomMovie(@PathVariable String roomCode,
                                             @RequestBody Map<String, String> payload) {
        String movieId = payload.get("movieId");

        return roomRepository.findByRoomCode(roomCode)
                .map(room -> {
                    room.setMovieId(movieId);
                    roomRepository.save(room);
                    return ResponseEntity.ok(room);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String generateUniqueRoomCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String code;

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            code = sb.toString();
        } while (roomRepository.findByRoomCode(code).isPresent());

        return code;
    }
}