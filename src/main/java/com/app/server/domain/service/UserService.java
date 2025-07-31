package com.app.server.domain.service;


import com.app.server.domain.User;
import com.app.server.domain.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserService {
    // Create - signUp
    /**
     * 사용자 회원가입 처리
     * @param email password nickname 회원가입 요청 (이메일, 비밀번호, 닉네임)
     * @return 생성된 사용자 엔티티
     */
    User signUp(String email, String pw, String nickname, UserRole role);
    // Read
    User findUserById(Long id);
    Optional<User> findUserByEmail(String email);
    List<User> findAllUsers();
    List<User> findActiveUsers();

    // Update
    User updateUser(Long id, UserUpdateCommand updateCommand);

    // Delete
    void deleteUser(Long id);
    void restoreUser(Long id);

    // Business logic
    boolean existsByEmail(String email);
    long countActiveUsers();
    /**
     * 사용자 정보 업데이트를 위한 전용 데이터 객체.
     * 이 record는 domain 계층에 속하며, infrastructure의 DTO와는 무관하다.
     */
    record UserUpdateCommand(String email, String nickname, String name,UserRole role) {}

}
