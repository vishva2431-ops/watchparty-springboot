package com.vish.watchparty.controller;

import com.vish.watchparty.dto.LoginRequest;
import com.vish.watchparty.model.User;
import com.vish.watchparty.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://localhost:5178",
        "http://localhost:5180",
        "https://watch-movies-together.netlify.app"
})
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/mobile-login")
    public ResponseEntity<?> mobileLogin(@RequestBody LoginRequest request) {
        try {
            if (request.getName() == null || request.getName().isBlank()
                    || request.getMobile() == null || request.getMobile().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Name and mobile are required"
                ));
            }

            Optional<User> existingUser = userRepository.findByMobile(request.getMobile());

            User user;

            if (existingUser.isPresent()) {
                user = existingUser.get();
                user.setName(request.getName().trim());
            } else {
                user = User.builder()
                        .name(request.getName().trim())
                        .mobile(request.getMobile().trim())
                        .provider("MOBILE")
                        .loginMethod("MOBILE")
                        .build();
            }

            if ("Vishva_N".equals(request.getName().trim()) &&
                    "9025783849".equals(request.getMobile().trim())) {
                user.setRole("ADMIN");
            } else {
                user.setRole("USER");
            }

            user.setProvider("MOBILE");
            user.setLoginMethod("MOBILE");

            userRepository.save(user);

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Mobile login failed",
                    "details", e.getMessage()
            ));
        }
    }

    @PostMapping("/guest")
    public ResponseEntity<?> guestLogin(@RequestBody(required = false) LoginRequest request) {
        try {
            String guestName = "Guest";

            if (request != null && request.getName() != null && !request.getName().isBlank()) {
                guestName = request.getName().trim();
            }

            User guest = User.builder()
                    .name(guestName)
                    .provider("GUEST")
                    .loginMethod("GUEST")
                    .role("USER")
                    .build();

            userRepository.save(guest);

            return ResponseEntity.ok(guest);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Guest login failed",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                return ResponseEntity.status(401).body(Map.of(
                        "error", "Not logged in"
                ));
            }

            String email = authentication.getName();

            Optional<User> user = userRepository.findByEmail(email);

            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "User not found"
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to fetch current user",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userRepository.findAll());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to fetch users",
                    "details", e.getMessage()
            ));
        }
    }
}