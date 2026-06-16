package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String name);
    @Query("SELECT t FROM Team t WHERE LOWER(t.name) = LOWER(:name)")
    Optional<Team> findByNameIgnoreCase(@Param("name") String name);
    Optional<Team> findByTag(String tag);
}
