package com.example.tournament_aggregator.domain.dto.subscription;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscriptionRequest {

    @NotNull(message = "User id must not be null")
    private Long userId;

    @NotNull(message = "Team id must not be null")
    private Long teamId;
}

