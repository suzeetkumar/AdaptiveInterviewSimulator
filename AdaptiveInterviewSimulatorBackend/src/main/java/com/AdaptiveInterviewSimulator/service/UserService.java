package com.AdaptiveInterviewSimulator.service;

import com.AdaptiveInterviewSimulator.dto.RegisterRequest;
import com.AdaptiveInterviewSimulator.model.User;
import com.AdaptiveInterviewSimulator.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerNewUser(RegisterRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        User u = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .createdAt(Instant.now())
                .build();
        return userRepository.save(u);
    }
}