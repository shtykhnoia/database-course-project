package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.UserMapper;
import com.example.ticketingsystem.dto.request.RoleRequest;
import com.example.ticketingsystem.dto.request.UserRequest;
import com.example.ticketingsystem.dto.response.UserResponse;
import com.example.ticketingsystem.model.User;
import com.example.ticketingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserWithRoles(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        User user = userMapper.toEntity(request);
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(createdUser, Set.of()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        User user = userMapper.toEntity(request);
        User updatedUser = userService.updateUser(id, user);
        return userService.getUserWithRoles(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/roles")
    public ResponseEntity<Void> assignRole(
            @PathVariable Long userId,
            @Valid @RequestBody RoleRequest request) {
        userService.assignRole(userId, request.getRoleName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{roleName}")
    public ResponseEntity<Void> removeRole(
            @PathVariable Long userId,
            @PathVariable String roleName) {
        userService.removeRole(userId, roleName);
        return ResponseEntity.noContent().build();
    }
}
