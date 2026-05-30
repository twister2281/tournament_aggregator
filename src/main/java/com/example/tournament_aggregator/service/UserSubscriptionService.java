package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.dto.subscription.UserSubscriptionRequest;
import com.example.tournament_aggregator.domain.dto.subscription.UserSubscriptionResponse;
import com.example.tournament_aggregator.domain.dto.subscription.view.SubscriptionItemView;
import com.example.tournament_aggregator.domain.dto.team.view.TeamCardView;

import java.util.List;

public interface UserSubscriptionService {

    UserSubscriptionResponse createUserSubscription(UserSubscriptionRequest request);

    UserSubscriptionResponse updateUserSubscription(Long id, UserSubscriptionRequest request);

    UserSubscriptionResponse getUserSubscriptionById(Long id);

    List<UserSubscriptionResponse> getAllUserSubscriptions();

    void deleteUserSubscription(Long id);

    List<SubscriptionItemView> getSubscriptionsForUser(Long userId);

    List<TeamCardView> getTeamCardsForUser(Long userId);

    void subscribeUserToTeam(Long userId, Long teamId);

    void unsubscribeUserFromTeam(Long userId, Long subscriptionId);

    void unsubscribeUserFromTeamByTeamId(Long userId, Long teamId);
}

