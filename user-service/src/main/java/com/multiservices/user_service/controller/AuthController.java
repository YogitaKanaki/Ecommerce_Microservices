package com.multiservices.user_service.controller;

import com.multiservices.user_service.dto.AuthDtos;
import com.multiservices.user_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService svc;

    public AuthController(AuthService svc) {
        this.svc = svc;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDtos.RegisterReq req) {
        svc.register(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.TokenRes> login(@Valid @RequestBody AuthDtos.LoginReq req) {
        return ResponseEntity.ok(svc.login(req));
    }
}

