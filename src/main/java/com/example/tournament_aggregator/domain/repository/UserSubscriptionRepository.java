package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.UserSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

	boolean existsByUserIdAndTeamId(Long userId, Long teamId);

	List<UserSubscription> findByUser_IdOrderBySubscribedAtDesc(Long userId);

	Optional<UserSubscription> findByUser_IdAndTeam_Id(Long userId, Long teamId);

	Optional<UserSubscription> findByIdAndUser_Id(Long id, Long userId);
}
