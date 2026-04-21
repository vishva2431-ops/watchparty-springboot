package com.vish.watchparty.controller;

import com.vish.watchparty.dto.GuestRequest;
import com.vish.watchparty.dto.LoginRequest;
import com.vish.watchparty.model.AppUser;
import com.vish.watchparty.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AppUserRepository appUserRepository;

    public AuthController(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @PostMapping("/mobile-login")
    public AppUser mobileLogin(@RequestBody LoginRequest request) {
        if (request.getName() == null || request.getName().isBlank()
                || request.getMobile() == null || request.getMobile().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and mobile are required");
        }

        AppUser user = appUserRepository.findByMobile(request.getMobile().trim()).orElseGet(AppUser::new);
        user.setName(request.getName().trim());
        user.setMobile(request.getMobile().trim());
        user.setProvider("LOCAL");
        user.setLoginMethod("MOBILE");
        user.setRole("USER");
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(Instant.now());
        }
        return appUserRepository.save(user);
    }

    @PostMapping("/guest")
    public AppUser guest(@RequestBody GuestRequest request) {
        String guestName = request.getName() == null || request.getName().isBlank()
                ? "Guest"
                : request.getName().trim();

        AppUser user = new AppUser();
        user.setName(guestName);
        user.setProvider("LOCAL");
        user.setLoginMethod("GUEST");
        user.setRole("GUEST");
        user.setCreatedAt(Instant.now());
        return appUserRepository.save(user);
    }

    @GetMapping("/users")
    public List<AppUser> users() {
        return appUserRepository.findAll();
    }
}