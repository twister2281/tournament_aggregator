package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.dto.prediction.PredictionRequest;
import com.example.tournament_aggregator.domain.dto.prediction.PredictionResponse;

import java.util.List;

public interface PredictionService {

    PredictionResponse createPrediction(PredictionRequest request);

    PredictionResponse updatePrediction(Long id, PredictionRequest request);

    PredictionResponse saveUserPrediction(Long userId, Long matchId, Long predictedWinnerId);

    PredictionResponse getPredictionById(Long id);

    List<PredictionResponse> getAllPredictions();

    List<PredictionResponse> getPredictionsForUserAndTournament(Long userId, Long tournamentId);

    void deletePrediction(Long id);
}
