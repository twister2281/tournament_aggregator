package com.example.tournament_aggregator.domain.dto.user;

import com.example.tournament_aggregator.domain.enums.AuthProvider;
import com.example.tournament_aggregator.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    private String email;

    private String password;
    private String firstName;
    private String lastName;
    private UserRole role;
    private Boolean isEnabled;
    private AuthProvider authProvider;
    private String providerId;
    private String avatarUrl;
}

