package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.auth.AuthResponse;
import com.example.ticketingsystem.dto.auth.LoginRequest;
import com.example.ticketingsystem.dto.auth.RegisterRequest;
import com.example.ticketingsystem.exception.DuplicateResourceException;
import com.example.ticketingsystem.exception.UnauthorizedException;
import com.example.ticketingsystem.model.User;
import com.example.ticketingsystem.repository.UserDAO;
import com.example.ticketingsystem.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Аутентификация", description = "Регистрация и вход пользователей")
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
    @Operation(summary = "Регистрация нового пользователя",
            description = "Создает нового пользователя с ролью USER и возвращает JWT токен")
    @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован")
    @ApiResponse(responseCode = "409", description = "Имя пользователя или email уже существует")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userDAO.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username", request.getUsername());
        }

        if (userDAO.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email", request.getEmail());
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
    @Operation(summary = "Вход в систему",
            description = "Аутентифицирует пользователя и возвращает JWT токен для доступа к защищенным эндпоинтам")
    @ApiResponse(responseCode = "200", description = "Успешный вход")
    @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        List<String> roleNames = userDAO.getUserRoleNames(user.getId());
        List<String> roles = roleNames.stream()
                .map(role -> "ROLE_" + role.toUpperCase())
                .toList();

        String token = jwtUtil.generateToken(user.getUsername(), roles);

        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getEmail(), roles));
    }
}
