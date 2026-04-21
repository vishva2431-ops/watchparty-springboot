package com.vish.watchparty.controller;

import com.vish.watchparty.dto.RoomCreateRequest;
import com.vish.watchparty.dto.RoomMovieUpdateRequest;
import com.vish.watchparty.model.Movie;
import com.vish.watchparty.model.Room;
import com.vish.watchparty.repository.MovieRepository;
import com.vish.watchparty.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;
    private final Random random = new Random();

    public RoomController(RoomRepository roomRepository, MovieRepository movieRepository) {
        this.roomRepository = roomRepository;
        this.movieRepository = movieRepository;
    }

    @PostMapping("/create")
    public Map<String, Object> createRoom(@RequestBody(required = false) RoomCreateRequest request) {
        Room room = new Room();
        room.setRoomCode(generateRoomCode());
        room.setCreatedBy(request != null && request.getUserName() != null ? request.getUserName().trim() : "Guest");
        room.setMovieId(request != null ? request.getMovieId() : null);
        room.setCreatedAt(Instant.now());

        Room savedRoom = roomRepository.save(room);
        return toResponse(savedRoom);
    }

    @GetMapping("/{roomCode}")
    public Map<String, Object> getRoom(@PathVariable String roomCode) {
        Room room = roomRepository.findByRoomCodeIgnoreCase(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        return toResponse(room);
    }

    @PutMapping("/{roomCode}/movie")
    public Map<String, Object> updateRoomMovie(@PathVariable String roomCode,
                                               @RequestBody RoomMovieUpdateRequest request) {
        Room room = roomRepository.findByRoomCodeIgnoreCase(roomCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        room.setMovieId(request.getMovieId());

        Room updatedRoom = roomRepository.save(room);
        return toResponse(updatedRoom);
    }

    private Map<String, Object> toResponse(Room room) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", room.getId());
        response.put("roomCode", room.getRoomCode());
        response.put("createdBy", room.getCreatedBy());
        response.put("currentQuality", room.getCurrentQuality());
        response.put("createdAt", room.getCreatedAt());

        if (room.getMovieId() != null && !room.getMovieId().isBlank()) {
            Optional<Movie> movie = movieRepository.findById(room.getMovieId());
            response.put("movie", movie.orElse(null));
        } else {
            response.put("movie", null);
        }

        return response;
    }

    private String generateRoomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        String code;
        do {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                builder.append(chars.charAt(random.nextInt(chars.length())));
            }
            code = builder.toString();
        } while (roomRepository.existsByRoomCodeIgnoreCase(code));
        return code;
    }
}