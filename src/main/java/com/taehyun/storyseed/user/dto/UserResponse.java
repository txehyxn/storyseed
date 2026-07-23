package com.taehyun.storyseed.user.dto;

import com.taehyun.storyseed.user.domain.User;
import com.taehyun.storyseed.user.domain.UserRole;

public record UserResponse(
        Long id,
        String email,
        String nickname,
        UserRole role
) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole()
        );
    }
}
