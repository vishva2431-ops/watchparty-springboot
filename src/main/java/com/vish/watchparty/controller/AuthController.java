package com.vish.watchparty.controller;

import com.vish.watchparty.dto.LoginRequest;
import com.vish.watchparty.model.User;
import com.vish.watchparty.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5180", allowCredentials = "true")
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
                    existing.setRole("USER");
                    return userRepository.save(existing);
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .name(request.getName())
                                .mobile(request.getMobile())
                                .provider("MOBILE")
                                .loginMethod("MOBILE")
                                .role("USER")
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
    public ResponseEntity<?> getCurrentUser(OAuth2AuthenticationToken authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return ResponseEntity.status(401).body("Not logged in");
        }

        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");

        if (email == null || email.isBlank()) {
            return ResponseEntity.status(404).body("Email not found");
        }

        return userRepository.findByEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body("User not found"));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}