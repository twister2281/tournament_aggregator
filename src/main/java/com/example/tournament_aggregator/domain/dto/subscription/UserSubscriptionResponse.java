package com.example.tournament_aggregator.domain.dto.subscription;

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
public class UserSubscriptionResponse {

    private Long id;
    private Long userId;
    private Long teamId;
    private LocalDateTime subscribedAt;
}

