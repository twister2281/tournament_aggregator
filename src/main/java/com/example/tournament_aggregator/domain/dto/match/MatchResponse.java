package com.example.tournament_aggregator.domain.dto.match;

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
public class MatchResponse {

    private Long id;
    private Long tournamentId;
    private Long team1Id;
    private Long team2Id;
    private Integer team1Score;
    private Integer team2Score;
    private LocalDateTime matchDate;
    private Boolean isCompleted;
    private Long winnerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

