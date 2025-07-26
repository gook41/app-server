package com.app.server.service;

import com.app.server.domain.User;
import com.app.server.exception.ResourceNotFoundException;
import com.app.server.exception.BadRequestException;
import com.app.server.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User saveUser(User user) {
        if (existsByEmail(user.getEmail())) {
            throw new BadRequestException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
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
    public User updateUser(Long id, User updatedUser) {
        User existingUser = findUserById(id);
        
        // Check if email is being changed and if new email already exists
        if (!existingUser.getEmail().equals(updatedUser.getEmail()) && 
            existsByEmail(updatedUser.getEmail())) {
            throw new BadRequestException("Email already exists: " + updatedUser.getEmail());
        }
        
        // Update fields
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setNickname(updatedUser.getNickname());
        existingUser.setName(updatedUser.getName());
        existingUser.setRole(updatedUser.getRole());
        
        // Password update only if provided
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(updatedUser.getPassword());
        }
        
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserById(id);
        user.setDeleted(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setDeleted(false);
        user.setUpdatedAt(LocalDateTime.now());
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
