package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
