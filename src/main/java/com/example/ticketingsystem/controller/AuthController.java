package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.auth.AuthResponse;
import com.example.ticketingsystem.dto.auth.LoginRequest;
import com.example.ticketingsystem.dto.auth.RegisterRequest;
import com.example.ticketingsystem.model.User;
import com.example.ticketingsystem.repository.UserDAO;
import com.example.ticketingsystem.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UserDAO userDAO, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userDAO = userDAO;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        if (userDAO.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        if (userDAO.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        User savedUser = userDAO.create(user);
        userDAO.assignRole(savedUser.getId(), "user");

        List<String> roles = List.of("ROLE_USER");
        String token = jwtUtil.generateToken(savedUser.getUsername(), roles);

        return ResponseEntity.ok(new AuthResponse(token, savedUser.getUsername(), savedUser.getEmail(), roles));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = userDAO.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        List<String> roleNames = userDAO.getUserRoleNames(user.getId());
        List<String> roles = roleNames.stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .toList();

        String token = jwtUtil.generateToken(user.getUsername(), roles);

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail(), roles));
    }
}
