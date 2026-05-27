package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.user.UserRequest;
import com.example.tournament_aggregator.domain.dto.user.UserResponse;
import com.example.tournament_aggregator.domain.entity.User;
import com.example.tournament_aggregator.domain.enums.AuthProvider;
import com.example.tournament_aggregator.domain.enums.UserRole;
import com.example.tournament_aggregator.domain.repository.UserRepository;
import com.example.tournament_aggregator.exception.ResourceNotFoundException;
import com.example.tournament_aggregator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        validate(request);
        ensureUnique(null, request);
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password must not be blank");
        }
        User user = User.builder()
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(normalize(request.getFirstName()))
                .lastName(normalize(request.getLastName()))
                .role(request.getRole() != null ? request.getRole() : UserRole.USER)
                .isEnabled(request.getIsEnabled() == null || request.getIsEnabled())
                .authProvider(request.getAuthProvider() != null ? request.getAuthProvider() : AuthProvider.LOCAL)
                .providerId(normalize(request.getProviderId()))
                .avatarUrl(normalize(request.getAvatarUrl()))
                .build();
        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        validate(request);
        User user = getUserEntityById(id);
        ensureUnique(user, request);
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setFirstName(normalize(request.getFirstName()));
        user.setLastName(normalize(request.getLastName()));
        user.setRole(request.getRole() != null ? request.getRole() : user.getRole());
        if (request.getIsEnabled() != null) {
            user.setIsEnabled(request.getIsEnabled());
        }
        user.setAuthProvider(request.getAuthProvider() != null ? request.getAuthProvider() : user.getAuthProvider());
        user.setProviderId(normalize(request.getProviderId()));
        user.setAvatarUrl(normalize(request.getAvatarUrl()));
        return toResponse(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toResponse(getUserEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.delete(getUserEntityById(id));
    }

    private User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private void ensureUnique(User currentUser, UserRequest request) {
        userRepository.findByUsername(request.getUsername())
                .filter(user -> currentUser == null || !user.getId().equals(currentUser.getId()))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username is already taken");
                });

        userRepository.findByEmail(request.getEmail())
                .filter(user -> currentUser == null || !user.getId().equals(currentUser.getId()))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email is already taken");
                });

        if (request.getProviderId() != null && !request.getProviderId().isBlank()) {
            userRepository.findByProviderId(request.getProviderId().trim())
                    .filter(user -> currentUser == null || !user.getId().equals(currentUser.getId()))
                    .ifPresent(user -> {
                        throw new IllegalArgumentException("Provider id is already taken");
                    });
        }
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .isEnabled(user.getIsEnabled())
                .authProvider(user.getAuthProvider())
                .providerId(user.getProviderId())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private void validate(UserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("User request must not be null");
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

