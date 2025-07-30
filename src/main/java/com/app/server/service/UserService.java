package com.app.server.service;

import com.app.server.domain.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    // Create - signUp
    User createUser(User.CreateRequest user);

    // Read
    User findUserById(Long id);
    Optional<User> findUserByEmail(String email);
    List<User> findAllUsers();
    List<User> findActiveUsers();

    // Update
    // User updateUser(Long id, User user);
    // 엔티티 대신 DTO를 받도록 수정. 이게 국룰임.
    User updateUser(Long id, User.UpdateRequest request);

    // Delete (soft delete)
    void deleteUser(Long id);
    void restoreUser(Long id);

    // Business logic
    boolean existsByEmail(String email);
    long countActiveUsers();
}
