package com.example.tournament_aggregator.domain.dto.playerstatistic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerStatisticRequest {

    @NotNull(message = "Match id must not be null")
    private Long matchId;

    @NotNull(message = "Team id must not be null")
    private Long teamId;

    @NotBlank(message = "Player name must not be blank")
    private String playerName;

    private String heroName;

    @PositiveOrZero(message = "Kills must be zero or positive")
    private Integer kills;

    @PositiveOrZero(message = "Deaths must be zero or positive")
    private Integer deaths;

    @PositiveOrZero(message = "Assists must be zero or positive")
    private Integer assists;

    @PositiveOrZero(message = "Last hits must be zero or positive")
    private Integer lastHits;

    @PositiveOrZero(message = "Denies must be zero or positive")
    private Integer denies;
}

