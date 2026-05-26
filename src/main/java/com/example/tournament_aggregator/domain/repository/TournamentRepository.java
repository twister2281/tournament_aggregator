package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {
}
