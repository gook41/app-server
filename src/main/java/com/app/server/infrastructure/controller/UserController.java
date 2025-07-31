package com.app.server.infrastructure.controller;


import com.app.server.application.mapper.UserMapper;
import com.app.server.domain.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "사용자 관리 API")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping("/{id}")
    public ResponseEntity<AuthController.UserResponse> getUserById(@PathVariable Long id) {
        var user = userService.findUserById(id);
        var responseDto = userMapper.toResponse(user);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthController.UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody AuthController.UserUpdateRequest request) {
        var command = new UserService.UserUpdateCommand(
                request.email(),
                request.nickname(),
                request.name(),
                request.role()
        );
        var user = userService.updateUser(id, command);
        var responseDto = userMapper.toResponse(user);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        // 내용이 없는 성공적인 응답은 204 No Content가 국룰
        return ResponseEntity.noContent().build();
    }

//    @GetMapping
//    public ResponseEntity<List<AuthController.UserResponse>> getAllUsers() {
//        var users = userService.findAllUsers();
//        var responseDtos = users.stream()
//                .map(userMapper::toResponse)
//                .toList();
//        return ResponseEntity.ok(responseDtos);
//    }

    @GetMapping
    public ResponseEntity<List<AuthController.UserResponse>> getActiveUsers() {
        var users = userService.findActiveUsers();
        var responseDtos = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }
}

