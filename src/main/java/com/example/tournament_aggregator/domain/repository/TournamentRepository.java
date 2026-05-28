package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

	Optional<Tournament> findByName(String name);
}
