package com.example.tournament_aggregator.domain.dto.user;

import com.example.tournament_aggregator.domain.enums.AuthProvider;
import com.example.tournament_aggregator.domain.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private UserRole role;
    private Boolean isEnabled;
    private AuthProvider authProvider;
    private String providerId;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

