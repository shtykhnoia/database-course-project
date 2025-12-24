package com.example.ticketingsystem.repository;

import com.example.ticketingsystem.mapper.RoleRowMapper;
import com.example.ticketingsystem.mapper.UserRowMapper;
import com.example.ticketingsystem.model.Role;
import com.example.ticketingsystem.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDAO {
    private final JdbcTemplate jdbcTemplate;


    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> getUserById(Long id) {
        String query = """
                SELECT id,
                username,
                email,
                password_hash,
                first_name,
                last_name,
                created_at
                FROM users
                WHERE id=?
                """;
        List<User> users = jdbcTemplate.query(query, new UserRowMapper(), id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    public List<User> getAllUsers() {
        String query = """
                SELECT id,
                username,
                email,
                password_hash,
                first_name,
                last_name,
                created_at
                FROM users
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(query, new UserRowMapper());
    }

    public List<User> getAllUsers(int page, int size) {
        String query = """
                SELECT id,
                username,
                email,
                password_hash,
                first_name,
                last_name,
                created_at
                FROM users
                ORDER BY created_at DESC
                LIMIT ? OFFSET ?
                """;
        int offset = page * size;
        return jdbcTemplate.query(query, new UserRowMapper(), size, offset);
    }

    public List<Role> getUserRoles(Long id) {
        String query = """
                SELECT r.id, r.name
                FROM user_roles ur
                JOIN roles r ON r.id = ur.role_id
                WHERE ur.user_id = ?
                """;
        return jdbcTemplate.query(query, new RoleRowMapper(), id);
    }

    public List<String> getUserRoleNames(Long id) {
        String query = """
                SELECT r.name
                FROM user_roles ur
                JOIN roles r ON r.id = ur.role_id
                WHERE ur.user_id = ?
                """;
        return jdbcTemplate.queryForList(query, String.class, id);
    }

    public User create(User user) {
        String query = """
                INSERT INTO users (username, email, password_hash, first_name, last_name)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, new String[]{"id"});
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFirstName());
            ps.setString(5, user.getLastName());
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    public Optional<User> findByEmail(String email) {
        String query = """
                SELECT id, username, email, password_hash, first_name, last_name, created_at
                FROM users
                WHERE email = ?
                """;
        List<User> users = jdbcTemplate.query(query, new UserRowMapper(), email);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    public Optional<User> findByUsername(String username) {
        String query = """
                SELECT id, username, email, password_hash, first_name, last_name, created_at
                FROM users
                WHERE username = ?
                """;
        List<User> users = jdbcTemplate.query(query, new UserRowMapper(), username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.getFirst());
    }

    public void assignRole(Long userId, String roleName) {
        String checkQuery = "SELECT COUNT(*) FROM roles WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(checkQuery, Integer.class, roleName);
        if (count == null || count == 0) {
            throw new IllegalArgumentException("Role not found: " + roleName);
        }

        String query = """
                INSERT INTO user_roles (user_id, role_id)
                SELECT ?, id FROM roles WHERE name = ?
                """;
        jdbcTemplate.update(query, userId, roleName);
    }

    public void removeRole(Long userId, String roleName) {
        String query = """
                DELETE FROM user_roles
                WHERE user_id = ? AND role_id = (SELECT id FROM roles WHERE name = ?)
                """;
        jdbcTemplate.update(query, userId, roleName);
    }

    public User update(User user) {
        String query = """
                UPDATE users
                SET username = ?,
                    email = ?,
                    first_name = ?,
                    last_name = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(query,
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getId());

        return user;
    }

    public void delete(Long id) {
        String query = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(query, id);
    }
}
