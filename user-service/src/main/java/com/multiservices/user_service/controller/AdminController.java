package com.multiservices.user_service.controller;

import com.multiservices.user_service.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/ping")
    public String ping() {
        return "ADMIN OK";
    }

    // ✅ Promote a user to admin (ADMIN only because /admin/** is protected)
    @PostMapping("/promote")
    public ResponseEntity<?> promote(@RequestParam String email) {
        authService.promoteToAdmin(email);
        return ResponseEntity.ok("Promoted to ADMIN");
    }
}
