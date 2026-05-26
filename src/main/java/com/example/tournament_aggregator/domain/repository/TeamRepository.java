package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
