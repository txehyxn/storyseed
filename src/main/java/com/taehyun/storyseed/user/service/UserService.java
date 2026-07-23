package com.taehyun.storyseed.user.service;

import com.taehyun.storyseed.user.domain.User;
import com.taehyun.storyseed.user.dto.SignUpRequest;
import com.taehyun.storyseed.user.dto.UserResponse;
import com.taehyun.storyseed.user.exception.DuplicateEmailException;
import com.taehyun.storyseed.user.exception.DuplicateNicknameException;
import com.taehyun.storyseed.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        String normalizedNickname = request.nickname().trim();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException();
        }
        if (userRepository.existsByNickname(normalizedNickname)) {
            throw new DuplicateNicknameException();
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        User user = User.createLocal(normalizedEmail, encodedPassword, normalizedNickname);
        User savedUser = userRepository.save(user);

        return UserResponse.from(savedUser);
    }
}
