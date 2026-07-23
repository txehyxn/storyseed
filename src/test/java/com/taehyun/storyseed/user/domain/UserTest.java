package com.taehyun.storyseed.user.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {

    @Test
    void createLocalSetsLocalAuthenticationDefaults() {
        User user = User.createLocal("  User@Example.COM  ", "encoded-password", "storyteller");

        assertEquals("user@example.com", user.getEmail());
        assertEquals("encoded-password", user.getPassword());
        assertEquals(AuthProvider.LOCAL, user.getProvider());
        assertNull(user.getProviderId());
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    void createKakaoAllowsNullEmailAndSetsKakaoAuthenticationDefaults() {
        User user = User.createKakao(null, "kakao-user", "123456789");

        assertNull(user.getEmail());
        assertEquals(AuthProvider.KAKAO, user.getProvider());
        assertNull(user.getPassword());
        assertEquals("123456789", user.getProviderId());
        assertEquals(UserRole.USER, user.getRole());
    }

    @Test
    void createKakaoNormalizesEmailWhenProvided() {
        User user = User.createKakao("  Kakao.User@Example.COM  ", "kakao-user", "123456789");

        assertEquals("kakao.user@example.com", user.getEmail());
    }

    @Test
    void createLocalRejectsNullOrBlankEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createLocal(null, "encoded-password", "storyteller")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createLocal("   ", "encoded-password", "storyteller")
        );
    }

    @Test
    void createLocalRejectsNullOrBlankPassword() {
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createLocal("user@example.com", null, "storyteller")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createLocal("user@example.com", " ", "storyteller")
        );
    }

    @Test
    void createLocalRejectsNullOrBlankNickname() {
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createLocal("user@example.com", "encoded-password", null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createLocal("user@example.com", "encoded-password", " ")
        );
    }

    @Test
    void createKakaoRejectsBlankEmail() {
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createKakao("   ", "kakao-user", "123456789")
        );
    }

    @Test
    void createKakaoRejectsNullOrBlankNickname() {
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createKakao(null, null, "123456789")
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createKakao(null, " ", "123456789")
        );
    }

    @Test
    void createKakaoRejectsNullOrBlankProviderId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createKakao(null, "kakao-user", null)
        );
        assertThrows(
                IllegalArgumentException.class,
                () -> User.createKakao(null, "kakao-user", " ")
        );
    }
}
