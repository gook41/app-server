package com.app.server.repository;

import com.app.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    
    List<User> findByDeletedFalse();
    
    boolean existsByEmail(String email);
    
    boolean existsByNickname(String nickname);
    
    long countByDeletedFalse();
}
