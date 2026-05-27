package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.auth.RegistrationRequest;
import com.example.tournament_aggregator.domain.entity.User;
import com.example.tournament_aggregator.domain.enums.AuthProvider;
import com.example.tournament_aggregator.domain.enums.UserRole;
import com.example.tournament_aggregator.domain.repository.UserRepository;
import com.example.tournament_aggregator.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(RegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration request must not be null");
        }
        if (userRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Email is already taken");
        }

        User user = User.builder()
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(normalize(request.getFirstName()))
                .lastName(normalize(request.getLastName()))
                .authProvider(AuthProvider.LOCAL)
                .role(UserRole.USER)
                .isEnabled(true)
                .build();

        return userRepository.save(user);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

