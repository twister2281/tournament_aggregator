package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    Optional<Prediction> findByUser_IdAndMatch_Id(Long userId, Long matchId);

    List<Prediction> findByUser_IdAndMatch_Tournament_Id(Long userId, Long tournamentId);
}
