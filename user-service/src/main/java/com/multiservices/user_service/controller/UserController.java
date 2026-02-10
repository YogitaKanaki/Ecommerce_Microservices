package com.multiservices.user_service.controller;

import com.multiservices.user_service.repo.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepo repo;

    public UserController(UserRepo repo) {
        this.repo = repo;
    }

    // ✅ Basic "me" endpoint (real-world)
    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        UUID userId = UUID.fromString(auth.getPrincipal().toString());
        var u = repo.findById(userId).orElseThrow();

        return Map.of(
                "id", u.getId(),
                "email", u.getEmail(),
                "firstName", u.getFirstName(),
                "lastName", u.getLastName(),
                "phone", u.getPhone(),
                "role", u.getRole().name(),
                "addressesCount", u.getAddresses().size()
        );
    }
}
