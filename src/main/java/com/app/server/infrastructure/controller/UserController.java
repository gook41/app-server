package com.app.server.infrastructure.controller;


import com.app.server.domain.User;
import com.app.server.mapper.UserMapper;
import com.app.server.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "사용자 관리 API")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;


    @PostMapping("/")
    public User.Response createUser(@Valid @RequestBody User.CreateRequest request) {
        // MapStruct를 사용한 DTO → Entity 변환
        User user = userMapper.toEntity(request);

        // 비즈니스 로직은 서비스에서 처리
        User savedUser = userService.saveUser(user);

        // MapStruct를 사용한 Entity → DTO 변환
        return userMapper.toResponse(savedUser);
    }

    @GetMapping("/{id}")
    public User.Response getUserById(@PathVariable Long id) {
        // 비즈니스 로직은 서비스에서 처리
        User user = userService.findUserById(id);

        // MapStruct를 사용한 Entity → DTO 변환
        return userMapper.toResponse(user);
    }

    @PutMapping("/{id}")
    public User.Response updateUser(@PathVariable Long id, @Valid @RequestBody User.UpdateRequest request) {
        // 기존 사용자 조회
        User existingUser = userService.findUserById(id);

        // UpdateRequest를 기존 엔티티에 매핑
        userMapper.updateEntity(request, existingUser);

        // 비즈니스 로직은 서비스에서 처리
        User updatedUser = userService.updateUser(id,request);

        // MapStruct를 사용한 Entity → DTO 변환
        return userMapper.toResponse(updatedUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        // 소프트 삭제 처리
        userService.deleteUser(id);
    }

    @GetMapping("/")
    public List<User.Response> getAllUsers() {
        // 전체 사용자 목록 조회
        List<User> users = userService.findAllUsers();

        // MapStruct를 사용한 Entity → DTO 변환
        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @GetMapping("/search")
    public User.Response getUserByEmail(@RequestParam String email) {
        // 이메일로 사용자 검색
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new com.app.server.exception.ResourceNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // MapStruct를 사용한 Entity → DTO 변환
        return userMapper.toResponse(user);
    }

}
