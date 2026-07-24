package com.taehyun.storyseed.user.controller;

import com.taehyun.storyseed.common.response.ApiResponse;
import com.taehyun.storyseed.config.jwt.JwtCookieProvider;
import com.taehyun.storyseed.user.domain.User;
import com.taehyun.storyseed.user.dto.LoginRequest;
import com.taehyun.storyseed.user.dto.LoginResponse;
import com.taehyun.storyseed.user.dto.SignUpRequest;
import com.taehyun.storyseed.user.dto.UserResponse;
import com.taehyun.storyseed.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtCookieProvider jwtCookieProvider;

    public UserController(UserService userService, JwtCookieProvider jwtCookieProvider) {
        this.userService = userService;
        this.jwtCookieProvider = jwtCookieProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponse>> signUp(
            @Valid @RequestBody SignUpRequest request
    ) {
        UserResponse response = userService.signUp(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpServletRequest
    ) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        jwtCookieProvider.createAccessTokenCookie(
                                response.accessToken(),
                                response.expiresIn(),
                                httpServletRequest.isSecure()
                        ).toString()
                )
                .body(ApiResponse.success(response));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal User user
    ) {
        return ResponseEntity.ok(ApiResponse.success(UserResponse.from(user)));
    }
}
