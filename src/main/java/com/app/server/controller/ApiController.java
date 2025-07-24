package com.app.server.controller;

import com.app.server.domain.User;
import com.app.server.mapper.UserMapper;
import com.app.server.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private final UserService userService;
    private final UserMapper userMapper;

    public ApiController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/users")
    public User.Response createUser(@Valid @RequestBody User.CreateRequest request) {
        // MapStruct를 사용한 DTO → Entity 변환
        User user = userMapper.toEntity(request);
        
        // 비즈니스 로직은 서비스에서 처리
        User savedUser = userService.saveUser(user);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return userMapper.toResponse(savedUser);
    }

    @GetMapping("/users/{id}")
    public User.Response getUserById(@PathVariable Long id) {
        // 비즈니스 로직은 서비스에서 처리
        User user = userService.findUserById(id);
        
        // MapStruct를 사용한 Entity → DTO 변환
        return userMapper.toResponse(user);
    }
}

