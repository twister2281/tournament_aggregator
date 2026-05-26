package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.PlayerStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStatisticRepository extends JpaRepository<PlayerStatistic, Long> {
}
