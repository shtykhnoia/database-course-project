package com.example.ticketingsystem.service;

import com.example.ticketingsystem.dto.response.UserResponse;
import com.example.ticketingsystem.exception.DuplicateResourceException;
import com.example.ticketingsystem.model.Role;
import com.example.ticketingsystem.model.User;
import com.example.ticketingsystem.repository.UserDAO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDAO userDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public Optional<User> getUserById(Long id) {
        return userDAO.getUserById(id);
    }

    public Optional<UserResponse> getUserWithRoles(Long id) {
        Optional<User> userOpt = userDAO.getUserById(id);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();
        List<Role> roles = userDAO.getUserRoles(id);

        Set<String> roleNames = roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        UserResponse response = new UserResponse(user, roleNames);
        return Optional.of(response);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Transactional
    public User createUser(User user) {
        if (userDAO.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email", user.getEmail());
        }

        if (userDAO.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username", user.getUsername());
        }

        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);

        User createdUser = userDAO.createUser(user);

        userDAO.assignRole(createdUser.getId(), "user");

        return createdUser;
    }

    @Transactional
    public User updateUser(Long id, User user) {
        User existing = userDAO.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!existing.getEmail().equals(user.getEmail()) &&
                userDAO.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email", user.getEmail());
        }

        if (!existing.getUsername().equals(user.getUsername()) &&
                userDAO.findByUsername(user.getUsername()).isPresent()) {
            throw new DuplicateResourceException("Username", user.getUsername());
        }

        user.setId(id);
        user.setPasswordHash(existing.getPasswordHash());
        user.setCreatedAt(existing.getCreatedAt());

        return userDAO.update(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        userDAO.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userDAO.delete(id);
    }

    public void assignRole(Long userId, String roleName) {
        userDAO.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userDAO.assignRole(userId, roleName);
    }

    public void removeRole(Long userId, String roleName) {
        userDAO.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userDAO.removeRole(userId, roleName);
    }
}