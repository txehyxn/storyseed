package com.taehyun.storyseed.user.service;

import com.taehyun.storyseed.config.jwt.JwtTokenProvider;
import com.taehyun.storyseed.user.domain.User;
import com.taehyun.storyseed.user.dto.LoginRequest;
import com.taehyun.storyseed.user.dto.LoginResponse;
import com.taehyun.storyseed.user.dto.SignUpRequest;
import com.taehyun.storyseed.user.dto.UserResponse;
import com.taehyun.storyseed.user.exception.DuplicateEmailException;
import com.taehyun.storyseed.user.exception.DuplicateNicknameException;
import com.taehyun.storyseed.user.exception.InvalidLoginException;
import com.taehyun.storyseed.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    void signUpNormalizesValuesEncryptsPasswordAndSavesUser() {
        SignUpRequest request = new SignUpRequest(
                "  User@Example.COM  ",
                "password123",
                "  taehyun  "
        );
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.signUp(request);

        verify(userRepository).existsByEmail("user@example.com");
        verify(userRepository).existsByNickname("taehyun");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertEquals("user@example.com", savedUser.getEmail());
        assertEquals("taehyun", savedUser.getNickname());
        assertNotEquals("password123", savedUser.getPassword());
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        assertEquals("user@example.com", response.email());
        assertEquals("taehyun", response.nickname());
    }

    @Test
    void signUpRejectsDuplicateNormalizedEmailWithoutSaving() {
        SignUpRequest request = new SignUpRequest(
                "  User@Example.COM  ",
                "password123",
                "taehyun"
        );
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThrows(DuplicateEmailException.class, () -> userService.signUp(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void signUpRejectsDuplicateTrimmedNicknameWithoutSaving() {
        SignUpRequest request = new SignUpRequest(
                "user@example.com",
                "password123",
                "  taehyun  "
        );
        when(userRepository.existsByNickname("taehyun")).thenReturn(true);

        assertThrows(DuplicateNicknameException.class, () -> userService.signUp(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginNormalizesEmailAndReturnsUserForMatchingPassword() {
        User user = User.createLocal(
                "user@example.com",
                passwordEncoder.encode("password123"),
                "taehyun"
        );
        when(userRepository.findByEmail("user@example.com")).thenReturn(java.util.Optional.of(user));

        when(jwtTokenProvider.createAccessToken(user)).thenReturn("access-token");
        when(jwtTokenProvider.getAccessTokenExpirationSeconds()).thenReturn(3600L);

        LoginResponse response = userService.login(
                new LoginRequest("  User@Example.COM  ", "password123")
        );

        verify(userRepository).findByEmail("user@example.com");
        verify(jwtTokenProvider).createAccessToken(user);
        assertEquals("access-token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(3600L, response.expiresIn());
        assertEquals("user@example.com", response.user().email());
        assertEquals("taehyun", response.user().nickname());
    }

    @Test
    void loginRejectsUnknownEmailWithCommonMessage() {
        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(java.util.Optional.empty());

        InvalidLoginException exception = assertThrows(
                InvalidLoginException.class,
                () -> userService.login(new LoginRequest("missing@example.com", "password123"))
        );

        assertEquals("이메일 또는 비밀번호가 올바르지 않습니다.", exception.getMessage());
    }

    @Test
    void loginRejectsWrongPasswordWithSameCommonMessage() {
        User user = User.createLocal(
                "user@example.com",
                passwordEncoder.encode("correct-password"),
                "taehyun"
        );
        when(userRepository.findByEmail("user@example.com")).thenReturn(java.util.Optional.of(user));

        InvalidLoginException exception = assertThrows(
                InvalidLoginException.class,
                () -> userService.login(new LoginRequest("user@example.com", "wrong-password"))
        );

        assertEquals("이메일 또는 비밀번호가 올바르지 않습니다.", exception.getMessage());
    }
}
