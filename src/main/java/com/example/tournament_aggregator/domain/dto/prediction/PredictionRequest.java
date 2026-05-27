package com.example.tournament_aggregator.domain.dto.prediction;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionRequest {

    @NotNull(message = "User id must not be null")
    private Long userId;

    @NotNull(message = "Match id must not be null")
    private Long matchId;

    private Long predictedWinnerId;
    private Boolean isCorrect;
}

