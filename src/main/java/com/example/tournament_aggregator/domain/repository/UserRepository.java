package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

