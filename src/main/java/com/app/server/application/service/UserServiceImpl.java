package com.app.server.application.service;


import com.app.server.domain.User;
import com.app.server.domain.UserRepository;
import com.app.server.domain.UserRole;
import com.app.server.domain.exceptions.DuplicateEmailException;
import com.app.server.domain.exceptions.DuplicateNicknameException;
import com.app.server.domain.service.UserService;
import com.app.server.infrastructure.exceptions.BadRequestException;
import com.app.server.infrastructure.exceptions.ResourceNotFoundException;
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
    private final PasswordEncoder passwordEncoder;

    @Override
    public User signUp(String email, String pw, String nickname, UserRole role) {
        if (existsByEmail(email)) {
            throw new BadRequestException("Email already exists: " + email);
        }
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(pw)) // 비밀번호 암호화
                .nickname(nickname)
                .role(UserRole.USER)
                .build();
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, UserUpdateCommand command) {
        User existingUser = findUserById(id);

        if (!existingUser.getNickname().equals(command.nickname()) &&
                userRepository.existsByNickname(command.nickname())) {
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다: " + command.nickname());
        }
        if (!existingUser.getEmail().equals(command.email()) &&
                userRepository.existsByEmail(command.email())) {
            throw new DuplicateEmailException("이미 사용 중인 E-mail입니다: " + command.email());
        }
        // 서비스 계층에서 직접 필드 업데이트
        if (command.nickname() != null) {
            existingUser.setNickname(command.nickname());
        }
        if (command.email() != null) {
            existingUser.setEmail(command.email());
        }
        if (command.name() != null) {
            existingUser.setName(command.name());
        }
        if (command.role() != null) {
            existingUser.setRole(command.role());
        }
        // @Transactional에 의해 더티 체킹으로 자동 저장됨
        return existingUser;
    }


    @Override
    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
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
    public void deleteUser(Long id) {
        User user = findUserById(id);
        user.setDeleted(true);
        userRepository.save(user);
    }

    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
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
