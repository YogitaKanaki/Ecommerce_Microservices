package com.multiservices.user_service.service;

import com.multiservices.user_service.dto.AuthDtos;
import com.multiservices.user_service.model.Address;
import com.multiservices.user_service.model.AppUser;
import com.multiservices.user_service.model.Role;
import com.multiservices.user_service.repo.UserRepo;
import com.multiservices.user_service.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        String email = req.email().toLowerCase();

        if (repo.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        AppUser u = new AppUser();
        u.setEmail(email);
        u.setPasswordHash(encoder.encode(req.password()));
        u.setRole(Role.USER);

        u.setFirstName(req.firstName());
        u.setLastName(req.lastName());
        u.setPhone(req.phone());

        if (req.address() != null) {
            var ar = req.address();

            Address a = new Address();
            a.setUser(u);
            a.setLine1(ar.line1());
            a.setLine2(ar.line2());
            a.setCity(ar.city());
            a.setState(ar.state());
            a.setPostalCode(ar.postalCode());
            a.setCountry(ar.country());
            a.setDefault(ar.isDefault());

            u.getAddresses().add(a);
        }

        repo.save(u);
    }

    public AuthDtos.TokenRes login(AuthDtos.LoginReq req) {
        String email = req.email().toLowerCase();

        var u = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email not registered"));

        if (!u.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User disabled");
        }

        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password");
        }

        String token = jwt.generate(u.getId(), u.getEmail(), u.getRole().name());
        return new AuthDtos.TokenRes(token);
    }

    public void promoteToAdmin(String email) {
        var u = repo.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        u.setRole(Role.ADMIN);
        repo.save(u);
    }
}
