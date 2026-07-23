package com.taehyun.storyseed.user.controller;

import com.taehyun.storyseed.user.domain.UserRole;
import com.taehyun.storyseed.user.dto.LoginRequest;
import com.taehyun.storyseed.user.dto.SignUpRequest;
import com.taehyun.storyseed.user.dto.UserResponse;
import com.taehyun.storyseed.user.exception.DuplicateEmailException;
import com.taehyun.storyseed.user.exception.DuplicateNicknameException;
import com.taehyun.storyseed.user.exception.InvalidLoginException;
import com.taehyun.storyseed.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void signUpReturnsCreatedWithoutAuthentication() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "user@example.com",
                "taehyun",
                UserRole.USER
        );
        when(userService.signUp(any(SignUpRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "password": "password123",
                                  "nickname": "taehyun"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("user@example.com"))
                .andExpect(jsonPath("$.data.nickname").value("taehyun"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.message").doesNotExist())
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void signUpReturnsConflictForDuplicateEmail() throws Exception {
        when(userService.signUp(any(SignUpRequest.class))).thenThrow(new DuplicateEmailException());

        performValidSignUp()
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("password123")
                )))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("stackTrace")
                )))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("SQL")
                )));
    }

    @Test
    void signUpReturnsConflictForDuplicateNickname() throws Exception {
        when(userService.signUp(any(SignUpRequest.class))).thenThrow(new DuplicateNicknameException());

        performValidSignUp()
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 닉네임입니다."));
    }

    @Test
    void signUpReturnsBadRequestWhenEmailIsMissing() throws Exception {
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "password": "password123",
                                  "nickname": "taehyun"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void signUpReturnsBadRequestForInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "invalid-email",
                                  "password": "password123",
                                  "nickname": "taehyun"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("올바른 이메일 형식이 아닙니다."));
    }

    @Test
    void signUpReturnsBadRequestForShortPassword() throws Exception {
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "password": "short",
                                  "nickname": "taehyun"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비밀번호는 8자 이상 72자 이하여야 합니다."));
    }

    @Test
    void signUpReturnsBadRequestForBlankNickname() throws Exception {
        mockMvc.perform(post("/api/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "user@example.com",
                                  "password": "password123",
                                  "nickname": " "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void loginReturnsUserWithoutAuthentication() throws Exception {
        UserResponse response = new UserResponse(
                1L,
                "user@test.com",
                "태현",
                UserRole.USER
        );
        when(userService.login(any(LoginRequest.class))).thenReturn(response);

        performValidLogin()
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("user@test.com"))
                .andExpect(jsonPath("$.data.nickname").value("태현"))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.message").doesNotExist())
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void loginReturnsUnauthorizedForUnknownEmail() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenThrow(new InvalidLoginException());

        performValidLogin()
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("이메일 또는 비밀번호가 올바르지 않습니다."))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void loginReturnsUnauthorizedForWrongPassword() throws Exception {
        when(userService.login(any(LoginRequest.class))).thenThrow(new InvalidLoginException());

        performValidLogin()
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message")
                        .value("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void loginReturnsBadRequestForInvalidInput() throws Exception {
        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "invalid-email",
                                  "password": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    private org.springframework.test.web.servlet.ResultActions performValidSignUp() throws Exception {
        return mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "user@example.com",
                          "password": "password123",
                          "nickname": "taehyun"
                        }
                        """));
    }

    private org.springframework.test.web.servlet.ResultActions performValidLogin() throws Exception {
        return mockMvc.perform(post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "email": "user@test.com",
                          "password": "password123"
                        }
                        """));
    }
}
