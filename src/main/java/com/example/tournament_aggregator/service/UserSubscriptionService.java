package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.dto.subscription.UserSubscriptionRequest;
import com.example.tournament_aggregator.domain.dto.subscription.UserSubscriptionResponse;

import java.util.List;

public interface UserSubscriptionService {

    UserSubscriptionResponse createUserSubscription(UserSubscriptionRequest request);

    UserSubscriptionResponse updateUserSubscription(Long id, UserSubscriptionRequest request);

    UserSubscriptionResponse getUserSubscriptionById(Long id);

    List<UserSubscriptionResponse> getAllUserSubscriptions();

    void deleteUserSubscription(Long id);
}

