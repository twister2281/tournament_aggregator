package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.auth.RegistrationRequest;
import com.example.tournament_aggregator.domain.entity.User;
import com.example.tournament_aggregator.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void registerShouldCreateUserWithEncodedPassword() {
        RegistrationRequest request = RegistrationRequest.builder()
                .username("newuser")
                .email("newuser@example.com")
                .password("password123")
                .confirmPassword("password123")
                .firstName("Timur")
                .lastName("Ivanov")
                .build();

        when(userRepository.existsByUsernameIgnoreCase("newuser")).thenReturn(false);
        when(userRepository.existsByEmailIgnoreCase("newuser@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = authService.register(request);

        assertEquals("newuser", savedUser.getUsername());
        assertEquals("newuser@example.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
    }

    @Test
    void registerShouldRejectDuplicateUsername() {
        RegistrationRequest request = RegistrationRequest.builder()
                .username("existing")
                .email("another@example.com")
                .password("password123")
                .confirmPassword("password123")
                .build();

        when(userRepository.existsByUsernameIgnoreCase("existing")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> authService.register(request));

        assertEquals("Username is already taken", exception.getMessage());
    }
}

