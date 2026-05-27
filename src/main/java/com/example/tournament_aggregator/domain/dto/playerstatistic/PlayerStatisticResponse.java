package com.example.tournament_aggregator.domain.dto.playerstatistic;

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
public class PlayerStatisticResponse {

    private Long id;
    private Long matchId;
    private Long teamId;
    private String playerName;
    private String heroName;
    private Integer kills;
    private Integer deaths;
    private Integer assists;
    private Integer lastHits;
    private Integer denies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

