package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
}

