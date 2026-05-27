package com.example.tournament_aggregator.domain.dto.match;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchRequest {

    @NotNull(message = "Tournament id must not be null")
    private Long tournamentId;

    @NotNull(message = "Team 1 id must not be null")
    private Long team1Id;

    @NotNull(message = "Team 2 id must not be null")
    private Long team2Id;

    @PositiveOrZero(message = "Team 1 score must be zero or positive")
    private Integer team1Score;

    @PositiveOrZero(message = "Team 2 score must be zero or positive")
    private Integer team2Score;

    private LocalDateTime matchDate;
    private Boolean isCompleted;
    private Long winnerId;
}

