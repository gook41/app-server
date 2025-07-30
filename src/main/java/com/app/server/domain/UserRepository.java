package com.app.server.domain;

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
    /**
     * 소셜 로그인 제공자와 제공자 ID로 사용자를 찾는다.
     * @param provider "google", "kakao" 등
     * @param providerId 소셜 로그인 제공자가 부여한 고유 ID
     */
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
