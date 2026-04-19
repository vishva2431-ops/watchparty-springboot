package com.vish.watchparty.controller;

import com.vish.watchparty.dto.LoginRequest;
import com.vish.watchparty.model.User;
import com.vish.watchparty.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/mobile-login")
    public ResponseEntity<?> mobileLogin(@RequestBody LoginRequest request) {
        if (request.getName() == null || request.getName().isBlank()
                || request.getMobile() == null || request.getMobile().isBlank()) {
            return ResponseEntity.badRequest().body("Name and mobile are required");
        }

        User user = userRepository.findByMobile(request.getMobile())
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setProvider("MOBILE");
                    existing.setLoginMethod("MOBILE");

                    if ("Vishva_N".equals(request.getName()) && "9025783849".equals(request.getMobile())) {
                        existing.setRole("ADMIN");
                    } else {
                        existing.setRole("USER");
                    }

                    return userRepository.save(existing);
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .name(request.getName())
                                .mobile(request.getMobile())
                                .provider("MOBILE")
                                .loginMethod("MOBILE")
                                .role(
                                        "Vishva_N".equals(request.getName()) && "9025783849".equals(request.getMobile())
                                                ? "ADMIN"
                                                : "USER"
                                )
                                .build()
                ));

        return ResponseEntity.ok(user);
    }

    @PostMapping("/guest")
    public ResponseEntity<?> guestLogin(@RequestBody(required = false) LoginRequest request) {
        String guestName = "Guest";
        if (request != null && request.getName() != null && !request.getName().isBlank()) {
            guestName = request.getName();
        }

        User guest = userRepository.save(
                User.builder()
                        .name(guestName)
                        .provider("GUEST")
                        .loginMethod("GUEST")
                        .role("USER")
                        .build()
        );

        return ResponseEntity.ok(guest);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("User not found"));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}