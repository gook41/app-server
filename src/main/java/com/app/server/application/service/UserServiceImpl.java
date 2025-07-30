package com.app.server.application.service;


import com.app.server.application.mapper.UserMapper;
import com.app.server.domain.User;
import com.app.server.domain.UserRepository;
import com.app.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User createUser(User.CreateRequest request) {
        if (existsByEmail(request.email())) {
            throw new BadRequestException("Email already exists: " + request.email());
        }
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // 비밀번호 암호화
                .nickname(request.nickname())
                .build();
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new com.app.server.exception.ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findByDeletedFalse();
    }

    @Override
    public User updateUser(Long id, User.UpdateRequest request) {
        User existingUser = findUserById(id);

        if (!existingUser.getNickname().equals(request.nickname()) &&
                userRepository.existsByNickname(request.nickname())) { // existsByNickname으로 수정
            throw new com.app.server.exception.DuplicateNicknameException("이미 사용 중인 닉네임입니다: " + request.nickname());
        }
        userMapper.updateEntity(request, existingUser);

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserById(id);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.app.server.exception.ResourceNotFoundException("User not found with id: " + id));
        user.setDeleted(false);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveUsers() {
        return userRepository.countByDeletedFalse();
    }
}
