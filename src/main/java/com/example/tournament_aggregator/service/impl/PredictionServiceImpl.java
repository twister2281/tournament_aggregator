package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.prediction.PredictionRequest;
import com.example.tournament_aggregator.domain.dto.prediction.PredictionResponse;
import com.example.tournament_aggregator.domain.entity.Match;
import com.example.tournament_aggregator.domain.entity.Prediction;
import com.example.tournament_aggregator.domain.entity.User;
import com.example.tournament_aggregator.domain.repository.MatchRepository;
import com.example.tournament_aggregator.domain.repository.PredictionRepository;
import com.example.tournament_aggregator.domain.repository.UserRepository;
import com.example.tournament_aggregator.exception.ResourceNotFoundException;
import com.example.tournament_aggregator.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PredictionServiceImpl implements PredictionService {

    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;

    @Override
    public PredictionResponse createPrediction(PredictionRequest request) {
        validate(request);
        Prediction prediction = Prediction.builder()
                .user(resolveUser(request.getUserId()))
                .match(resolveMatch(request.getMatchId()))
                .predictedWinnerId(request.getPredictedWinnerId())
                .isCorrect(request.getIsCorrect())
                .build();
        return toResponse(predictionRepository.save(prediction));
    }

    @Override
    public PredictionResponse updatePrediction(Long id, PredictionRequest request) {
        validate(request);
        Prediction prediction = getPredictionEntityById(id);
        prediction.setUser(resolveUser(request.getUserId()));
        prediction.setMatch(resolveMatch(request.getMatchId()));
        prediction.setPredictedWinnerId(request.getPredictedWinnerId());
        prediction.setIsCorrect(request.getIsCorrect());
        return toResponse(predictionRepository.save(prediction));
    }

    @Override
    @Transactional(readOnly = true)
    public PredictionResponse getPredictionById(Long id) {
        return toResponse(getPredictionEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionResponse> getAllPredictions() {
        return predictionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public void deletePrediction(Long id) {
        predictionRepository.delete(getPredictionEntityById(id));
    }

    private Prediction getPredictionEntityById(Long id) {
        return predictionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prediction", id));
    }

    private User resolveUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private Match resolveMatch(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
    }

    private PredictionResponse toResponse(Prediction prediction) {
        return PredictionResponse.builder()
                .id(prediction.getId())
                .userId(prediction.getUser() != null ? prediction.getUser().getId() : null)
                .matchId(prediction.getMatch() != null ? prediction.getMatch().getId() : null)
                .predictedWinnerId(prediction.getPredictedWinnerId())
                .isCorrect(prediction.getIsCorrect())
                .createdAt(prediction.getCreatedAt())
                .updatedAt(prediction.getUpdatedAt())
                .build();
    }

    private void validate(PredictionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Prediction request must not be null");
        }
    }
}

