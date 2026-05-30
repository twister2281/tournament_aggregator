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

import java.util.Comparator;
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
        return savePrediction(
                resolveUser(request.getUserId()),
                resolveMatch(request.getMatchId()),
                request.getPredictedWinnerId()
        );
    }

    @Override
    public PredictionResponse updatePrediction(Long id, PredictionRequest request) {
        validate(request);
        Prediction prediction = getPredictionEntityById(id);
        prediction.setUser(resolveUser(request.getUserId()));
        prediction.setMatch(resolveMatch(request.getMatchId()));
        prediction.setPredictedWinnerId(request.getPredictedWinnerId());
        validatePredictionTarget(prediction.getMatch(), request.getPredictedWinnerId());
        prediction.setIsCorrect(resolveCorrectness(prediction.getMatch(), request.getPredictedWinnerId()));
        return toResponse(predictionRepository.save(prediction));
    }

    @Override
    public PredictionResponse saveUserPrediction(Long userId, Long matchId, Long predictedWinnerId) {
        User user = resolveUser(userId);
        Match match = resolveMatch(matchId);
        return savePrediction(user, match, predictedWinnerId);
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
    @Transactional(readOnly = true)
    public List<PredictionResponse> getPredictionsForUserAndTournament(Long userId, Long tournamentId) {
        if (userId == null || tournamentId == null) {
            return List.of();
        }

        return predictionRepository.findByUser_IdAndMatch_Tournament_Id(userId, tournamentId).stream()
                .sorted(Comparator.comparing(
                        prediction -> prediction.getMatch() != null ? prediction.getMatch().getMatchDate() : null,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toResponse)
                .toList();
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

    private PredictionResponse savePrediction(User user, Match match, Long predictedWinnerId) {
        validatePredictionTarget(match, predictedWinnerId);

        Prediction prediction = predictionRepository.findByUser_IdAndMatch_Id(user.getId(), match.getId())
                .orElseGet(Prediction::new);
        prediction.setUser(user);
        prediction.setMatch(match);
        prediction.setPredictedWinnerId(predictedWinnerId);
        prediction.setIsCorrect(resolveCorrectness(match, predictedWinnerId));
        return toResponse(predictionRepository.save(prediction));
    }

    private void validatePredictionTarget(Match match, Long predictedWinnerId) {
        if (match == null) {
            throw new IllegalArgumentException("Match must not be null");
        }
        if (Boolean.TRUE.equals(match.getIsCompleted())) {
            throw new IllegalArgumentException("Predictions are allowed only for pending matches");
        }
        if (predictedWinnerId == null) {
            throw new IllegalArgumentException("Predicted winner must not be null");
        }

        Long team1Id = match.getTeam1() != null ? match.getTeam1().getId() : null;
        Long team2Id = match.getTeam2() != null ? match.getTeam2().getId() : null;
        if (!predictedWinnerId.equals(team1Id) && !predictedWinnerId.equals(team2Id)) {
            throw new IllegalArgumentException("Predicted winner must be one of the match teams");
        }
    }

    private Boolean resolveCorrectness(Match match, Long predictedWinnerId) {
        if (match == null || predictedWinnerId == null || !Boolean.TRUE.equals(match.getIsCompleted())) {
            return null;
        }
        return predictedWinnerId.equals(match.getWinnerId());
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
