package com.example.ticketingsystem.controller;

import com.example.ticketingsystem.dto.mapper.UserMapper;
import com.example.ticketingsystem.dto.request.RoleRequest;
import com.example.ticketingsystem.dto.request.UserRequest;
import com.example.ticketingsystem.dto.response.UserResponse;
import com.example.ticketingsystem.model.User;
import com.example.ticketingsystem.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "Управление пользователями и их ролями (только для ADMIN)")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей системы. Поддерживает пагинацию.")
    @ApiResponse(responseCode = "200", description = "Список пользователей")
    @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN")
    public List<User> getAllUsers(
            @Parameter(description = "Номер страницы (начиная с 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы (максимум 100)") @RequestParam(defaultValue = "20") int size) {

        if (size > 100) {
            size = 100;
        }
        if (size < 1) {
            size = 20;
        }
        if (page < 0) {
            page = 0;
        }

        return userService.getAllUsers(page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает информацию о пользователе с его ролями")
    @ApiResponse(responseCode = "200", description = "Пользователь найден")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN")
    public ResponseEntity<UserResponse> getUserById(@Parameter(description = "ID пользователя") @PathVariable Long id) {
        return userService.getUserWithRoles(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать пользователя", description = "Создает нового пользователя с ролью USER по умолчанию")
    @ApiResponse(responseCode = "201", description = "Пользователь создан")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "409", description = "Email или username уже существует")
    @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        User user = userMapper.toEntity(request);
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(createdUser, Set.of()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя", description = "Обновляет данные пользователя (пароль не изменяется)")
    @ApiResponse(responseCode = "200", description = "Пользователь обновлен")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "409", description = "Email или username занят")
    @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID пользователя") @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        User user = userMapper.toEntity(request);
        User updatedUser = userService.updateUser(id, user);
        return userService.getUserWithRoles(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя", description = "Удаляет пользователя из системы")
    @ApiResponse(responseCode = "204", description = "Пользователь удален")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID пользователя") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/roles")
    @Operation(summary = "Назначить роль пользователю", description = "Добавляет роль пользователю (admin, organizer, user)")
    @ApiResponse(responseCode = "200", description = "Роль назначена")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN")
    public ResponseEntity<Void> assignRole(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Valid @RequestBody RoleRequest request) {
        userService.assignRole(userId, request.getRoleName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{roleName}")
    @Operation(summary = "Удалить роль у пользователя", description = "Удаляет указанную роль у пользователя")
    @ApiResponse(responseCode = "204", description = "Роль удалена")
    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    @ApiResponse(responseCode = "403", description = "Требуется роль ADMIN")
    public ResponseEntity<Void> removeRole(
            @Parameter(description = "ID пользователя") @PathVariable Long userId,
            @Parameter(description = "Название роли") @PathVariable String roleName) {
        userService.removeRole(userId, roleName);
        return ResponseEntity.noContent().build();
    }
}
