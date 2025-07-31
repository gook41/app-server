package com.app.server.service;

import com.app.server.application.mapper.UserMapper;
import com.app.server.application.service.UserServiceImpl;
import com.app.server.domain.User;
import com.app.server.domain.UserRepository;
import com.app.server.domain.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("testuser")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();
    }

//    @Test
//    @DisplayName("사용자 생성 성공")
//    void createUser_Success() {
//        // Given
//        User.CreateRequest request = new User.CreateRequest("new@example.com", "password", "newuser");
//        when(userRepository.existsByEmail(request.email())).thenReturn(false);
//        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");
//        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // When
//        User createdUser = userService.createUser(request);
//
//        // Then
//        assertThat(createdUser).isNotNull();
//        assertThat(createdUser.getEmail()).isEqualTo(request.email());
//        assertThat(createdUser.getPassword()).isEqualTo("encodedPassword");
//        verify(userRepository).save(any(User.class));
//    }

    // ... other tests
}