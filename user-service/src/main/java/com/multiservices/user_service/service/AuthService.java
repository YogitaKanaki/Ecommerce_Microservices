package com.multiservices.user_service.service;


import com.multiservices.user_service.dto.AuthDtos;
import com.multiservices.user_service.model.AppUser;
import com.multiservices.user_service.repo.UserRepo;
import com.multiservices.user_service.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepo repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthService(UserRepo repo, PasswordEncoder encoder, JwtUtil jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public void register(AuthDtos.RegisterReq req) {
        if (repo.existsByEmail(req.email().toLowerCase())) {
            throw new IllegalArgumentException("email already registered");
        }
        AppUser u = new AppUser();
        u.setEmail(req.email().toLowerCase());
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole("USER");
        repo.save(u);
    }

    public AuthDtos.TokenRes login(AuthDtos.LoginReq req) {
        var u = repo.findByEmail(req.email().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("invalid credentials"));

        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new IllegalArgumentException("invalid credentials");
        }

        String token = jwt.generate(u.getId(), u.getEmail(), u.getRole());
        return new AuthDtos.TokenRes(token);
    }
}

