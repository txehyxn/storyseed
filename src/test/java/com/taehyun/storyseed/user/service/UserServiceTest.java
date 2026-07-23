package com.taehyun.storyseed.user.service;

import com.taehyun.storyseed.user.domain.User;
import com.taehyun.storyseed.user.dto.SignUpRequest;
import com.taehyun.storyseed.user.dto.UserResponse;
import com.taehyun.storyseed.user.exception.DuplicateEmailException;
import com.taehyun.storyseed.user.exception.DuplicateNicknameException;
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

    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
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
}
