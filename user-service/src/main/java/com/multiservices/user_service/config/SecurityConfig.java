package com.multiservices.user_service.config;

import com.multiservices.user_service.model.AppUser;
import com.multiservices.user_service.model.Role;
import com.multiservices.user_service.repo.UserRepo;
import com.multiservices.user_service.security.JwtAuthFilter;
import com.multiservices.user_service.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public JwtUtil jwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.ttlSeconds}") long ttlSeconds
    ) {
        return new JwtUtil(secret, ttlSeconds);
    }

    @Bean
    CommandLineRunner initAdmin(UserRepo repo, PasswordEncoder encoder) {
        return args -> {
            if (!repo.existsByEmail("admin@gmail.com")) {
                AppUser admin = new AppUser();
                admin.setEmail("admin@gmail.com");
                admin.setPasswordHash(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setFirstName("System");
                admin.setLastName("Admin");
                repo.save(admin);
            }
        };
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Value("${app.jwt.secret}") String secret
    ) throws Exception {

        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.formLogin(form -> form.disable());
        http.httpBasic(basic -> basic.disable());

        http.addFilterBefore(new JwtAuthFilter(secret), UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/error").permitAll()

                // ADMIN only routes
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // USER or ADMIN routes
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")

                .anyRequest().authenticated()
        );

        return http.build();
    }
}
