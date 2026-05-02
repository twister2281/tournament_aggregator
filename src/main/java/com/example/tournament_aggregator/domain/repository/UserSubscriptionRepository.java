package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
}

