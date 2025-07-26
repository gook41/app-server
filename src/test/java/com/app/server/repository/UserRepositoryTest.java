package com.app.server.repository;

import com.app.server.domain.User;
import com.app.server.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository 통합 테스트")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;
    private User deletedUser;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        testUser1 = new User();
        testUser1.setEmail("test1@example.com");
        testUser1.setNickname("testuser1");
        testUser1.setPassword("password1");
        testUser1.setName("Test User 1");
        testUser1.setRole(UserRole.USER);
        testUser1.setDeleted(false);
        testUser1.setCreatedBy("system");
        testUser1.setUpdatedBy("system");
        testUser1.setCreatedAt(now);
        testUser1.setUpdatedAt(now);

        testUser2 = new User();
        testUser2.setEmail("test2@example.com");
        testUser2.setNickname("testuser2");
        testUser2.setPassword("password2");
        testUser2.setName("Test User 2");
        testUser2.setRole(UserRole.ADMIN);
        testUser2.setDeleted(false);
        testUser2.setCreatedBy("system");
        testUser2.setUpdatedBy("system");
        testUser2.setCreatedAt(now);
        testUser2.setUpdatedAt(now);

        deletedUser = new User();
        deletedUser.setEmail("deleted@example.com");
        deletedUser.setNickname("deleteduser");
        deletedUser.setPassword("password");
        deletedUser.setName("Deleted User");
        deletedUser.setRole(UserRole.USER);
        deletedUser.setDeleted(true);
        deletedUser.setCreatedBy("system");
        deletedUser.setUpdatedBy("system");
        deletedUser.setCreatedAt(now);
        deletedUser.setUpdatedAt(now);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void findByEmail_Success() {
        // Given
        entityManager.persistAndFlush(testUser1);

        // When
        Optional<User> foundUser = userRepository.findByEmail("test1@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test1@example.com");
        assertThat(foundUser.get().getNickname()).isEqualTo("testuser1");
        assertThat(foundUser.get().getRole()).isEqualTo(UserRole.USER);
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 사용자 조회 실패")
    void findByEmail_NotFound() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("삭제된 사용자도 이메일로 조회 가능")
    void findByEmail_DeletedUser() {
        // Given
        entityManager.persistAndFlush(deletedUser);

        // When
        Optional<User> foundUser = userRepository.findByEmail("deleted@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().isDeleted()).isTrue();
    }

    @Test
    @DisplayName("삭제되지 않은 사용자만 조회")
    void findByDeletedFalse_Success() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(deletedUser);

        // When
        List<User> activeUsers = userRepository.findByDeletedFalse();

        // Then
        assertThat(activeUsers).hasSize(2);
        assertThat(activeUsers).extracting(User::getEmail)
                .containsExactlyInAnyOrder("test1@example.com", "test2@example.com");
        assertThat(activeUsers).allMatch(user -> !user.isDeleted());
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하는 경우")
    void existsByEmail_Exists() {
        // Given
        entityManager.persistAndFlush(testUser1);

        // When
        boolean exists = userRepository.existsByEmail("test1@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 확인 - 존재하지 않는 경우")
    void existsByEmail_NotExists() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("삭제된 사용자의 이메일도 존재로 확인")
    void existsByEmail_DeletedUser() {
        // Given
        entityManager.persistAndFlush(deletedUser);

        // When
        boolean exists = userRepository.existsByEmail("deleted@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임 존재 여부 확인 - 존재하는 경우")
    void existsByNickname_Exists() {
        // Given
        entityManager.persistAndFlush(testUser1);

        // When
        boolean exists = userRepository.existsByNickname("testuser1");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("닉네임 존재 여부 확인 - 존재하지 않는 경우")
    void existsByNickname_NotExists() {
        // When
        boolean exists = userRepository.existsByNickname("nonexistent");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("삭제된 사용자의 닉네임도 존재로 확인")
    void existsByNickname_DeletedUser() {
        // Given
        entityManager.persistAndFlush(deletedUser);

        // When
        boolean exists = userRepository.existsByNickname("deleteduser");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("활성 사용자 수 조회")
    void countByDeletedFalse_Success() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(deletedUser);

        // When
        long count = userRepository.countByDeletedFalse();

        // Then
        assertThat(count).isEqualTo(2L);
    }

    @Test
    @DisplayName("활성 사용자가 없는 경우 0 반환")
    void countByDeletedFalse_NoActiveUsers() {
        // Given
        entityManager.persistAndFlush(deletedUser);

        // When
        long count = userRepository.countByDeletedFalse();

        // Then
        assertThat(count).isEqualTo(0L);
    }

    @Test
    @DisplayName("사용자 저장 및 자동 생성 필드 확인")
    void save_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        User newUser = new User();
        newUser.setEmail("new@example.com");
        newUser.setNickname("newuser");
        newUser.setPassword("password");
        newUser.setName("New User");
        newUser.setRole(UserRole.USER);
        newUser.setDeleted(false);
        newUser.setCreatedBy("system");
        newUser.setUpdatedBy("system");
        newUser.setCreatedAt(now);
        newUser.setUpdatedAt(now);

        // When
        User savedUser = userRepository.save(newUser);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(savedUser.getNickname()).isEqualTo("newuser");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.USER);
        assertThat(savedUser.isDeleted()).isFalse();
    }

    @Test
    @DisplayName("사용자 수정 시 updatedAt 수동 갱신")
    void update_UpdatedAtManualUpdate() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);
        LocalDateTime originalUpdatedAt = savedUser.getUpdatedAt();
        
        // 시간 차이를 만들기 위해 잠시 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // When
        savedUser.setName("Updated Name");
        savedUser.setUpdatedAt(LocalDateTime.now()); // 수동으로 updatedAt 갱신
        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        // Then
        assertThat(updatedUser.getUpdatedAt()).isAfter(originalUpdatedAt);
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("이메일 유니크 제약 조건 테스트")
    void save_DuplicateEmail_ThrowsException() {
        // Given
        entityManager.persistAndFlush(testUser1);
        
        User duplicateEmailUser = new User();
        duplicateEmailUser.setEmail("test1@example.com"); // 중복 이메일
        duplicateEmailUser.setNickname("different");
        duplicateEmailUser.setPassword("password");
        duplicateEmailUser.setRole(UserRole.USER);
        duplicateEmailUser.setCreatedBy("system");
        duplicateEmailUser.setUpdatedBy("system");

        // When & Then
        assertThatThrownBy(() -> {
            userRepository.save(duplicateEmailUser);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("닉네임 유니크 제약 조건 테스트")
    void save_DuplicateNickname_ThrowsException() {
        // Given
        entityManager.persistAndFlush(testUser1);
        
        User duplicateNicknameUser = new User();
        duplicateNicknameUser.setEmail("different@example.com");
        duplicateNicknameUser.setNickname("testuser1"); // 중복 닉네임
        duplicateNicknameUser.setPassword("password");
        duplicateNicknameUser.setRole(UserRole.USER);
        duplicateNicknameUser.setCreatedBy("system");
        duplicateNicknameUser.setUpdatedBy("system");

        // When & Then
        assertThatThrownBy(() -> {
            userRepository.save(duplicateNicknameUser);
            entityManager.flush();
        }).isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("사용자 삭제 (실제 삭제)")
    void delete_Success() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);
        Long userId = savedUser.getId();

        // When
        userRepository.delete(savedUser);
        entityManager.flush();

        // Then
        Optional<User> deletedUser = userRepository.findById(userId);
        assertThat(deletedUser).isEmpty();
    }

    @Test
    @DisplayName("모든 사용자 조회")
    void findAll_Success() {
        // Given
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
        entityManager.persistAndFlush(deletedUser);

        // When
        List<User> allUsers = userRepository.findAll();

        // Then
        assertThat(allUsers).hasSize(3);
        assertThat(allUsers).extracting(User::getEmail)
                .containsExactlyInAnyOrder("test1@example.com", "test2@example.com", "deleted@example.com");
    }

    @Test
    @DisplayName("ID로 사용자 조회")
    void findById_Success() {
        // Given
        User savedUser = entityManager.persistAndFlush(testUser1);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test1@example.com");
    }
}