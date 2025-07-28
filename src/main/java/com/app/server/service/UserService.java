package com.app.server.service;

import com.app.server.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    // Create
    User saveUser(User user);
    
    // Read
    User findUserById(Long id);
    Optional<User> findUserByEmail(String email);
    List<User> findAllUsers();
    List<User> findActiveUsers();
    
    // Update
    User updateUser(Long id, User user);
    
    // Delete (soft delete)
    void deleteUser(Long id);
    void restoreUser(Long id);
    
    // Business logic
    boolean existsByEmail(String email);
    long countActiveUsers();
}
