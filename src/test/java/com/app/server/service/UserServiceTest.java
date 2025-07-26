package com.app.server.service;

import com.app.server.domain.User;
import com.app.server.domain.UserRole;
import com.app.server.exception.BadRequestException;
import com.app.server.exception.ResourceNotFoundException;
import com.app.server.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setNickname("testuser");
        testUser.setName("Test User");
        testUser.setRole(UserRole.USER);
        testUser.setDeleted(false);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("사용자 저장 성공")
    void saveUser_Success() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User savedUser = userService.saveUser(testUser);

        // Then
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo(testUser.getEmail());
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("중복 이메일로 사용자 저장 실패")
    void saveUser_DuplicateEmail_ThrowsException() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.saveUser(testUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already exists");
        
        verify(userRepository).existsByEmail(testUser.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("ID로 사용자 조회 성공")
    void findUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        User foundUser = userService.findUserById(1L);

        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getId()).isEqualTo(1L);
        assertThat(foundUser.getEmail()).isEqualTo(testUser.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 사용자 조회 실패")
    void findUserById_NotFound_ThrowsException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.findUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");
        
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("이메일로 사용자 조회 성공")
    void findUserByEmail_Success() {
        // Given
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // When
        Optional<User> foundUser = userService.findUserByEmail(testUser.getEmail());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(testUser.getEmail());
        verify(userRepository).findByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("전체 사용자 목록 조회")
    void findAllUsers_Success() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@example.com");
        user2.setNickname("testuser2");
        
        List<User> userList = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(userList);

        // When
        List<User> foundUsers = userService.findAllUsers();

        // Then
        assertThat(foundUsers).hasSize(2);
        assertThat(foundUsers).contains(testUser, user2);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("활성 사용자 목록 조회")
    void findActiveUsers_Success() {
        // Given
        List<User> activeUsers = Arrays.asList(testUser);
        when(userRepository.findByDeletedFalse()).thenReturn(activeUsers);

        // When
        List<User> foundUsers = userService.findActiveUsers();

        // Then
        assertThat(foundUsers).hasSize(1);
        assertThat(foundUsers.get(0).isDeleted()).isFalse();
        verify(userRepository).findByDeletedFalse();
    }

    @Test
    @DisplayName("사용자 정보 수정 성공")
    void updateUser_Success() {
        // Given
        User updatedUser = new User();
        updatedUser.setEmail("updated@example.com");
        updatedUser.setNickname("updateduser");
        updatedUser.setName("Updated User");
        updatedUser.setRole(UserRole.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUser(1L, updatedUser);

        // Then
        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("updated@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("중복 이메일로 사용자 수정 실패")
    void updateUser_DuplicateEmail_ThrowsException() {
        // Given
        User updatedUser = new User();
        updatedUser.setEmail("duplicate@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(1L, updatedUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email already exists");
        
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("duplicate@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("사용자 소프트 삭제 성공")
    void deleteUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("사용자 복원 성공")
    void restoreUser_Success() {
        // Given
        testUser.setDeleted(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.restoreUser(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이메일 존재 여부 확인")
    void existsByEmail_Success() {
        // Given
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);

        // When
        boolean exists = userService.existsByEmail(testUser.getEmail());

        // Then
        assertThat(exists).isTrue();
        verify(userRepository).existsByEmail(testUser.getEmail());
    }

    @Test
    @DisplayName("활성 사용자 수 조회")
    void countActiveUsers_Success() {
        // Given
        when(userRepository.countByDeletedFalse()).thenReturn(5L);

        // When
        long count = userService.countActiveUsers();

        // Then
        assertThat(count).isEqualTo(5L);
        verify(userRepository).countByDeletedFalse();
    }
}