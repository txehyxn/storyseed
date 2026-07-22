package com.taehyun.storyseed.user.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void createLocalSetsLocalAuthenticationDefaults() {
        User user = User.createLocal("user@example.com", "encoded-password", "storyteller");

        assertEquals(AuthProvider.LOCAL, user.getProvider());
        assertNull(user.getProviderId());
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    void createKakaoSetsKakaoAuthenticationDefaults() {
        User user = User.createKakao(null, "kakao-user", "123456789");

        assertEquals(AuthProvider.KAKAO, user.getProvider());
        assertNull(user.getPassword());
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    void createLocalRejectsMissingRequiredValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createLocal(null, "encoded-password", "storyteller")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createLocal("user@example.com", " ", "storyteller")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createLocal("user@example.com", "encoded-password", "")
        );
    }

    @Test
    void createKakaoRejectsMissingRequiredValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createKakao(null, " ", "123456789")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createKakao(null, "kakao-user", null)
        );
    }
}
