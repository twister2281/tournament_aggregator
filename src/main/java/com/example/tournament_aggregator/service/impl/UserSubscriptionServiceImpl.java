package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.subscription.UserSubscriptionRequest;
import com.example.tournament_aggregator.domain.dto.subscription.UserSubscriptionResponse;
import com.example.tournament_aggregator.domain.entity.Team;
import com.example.tournament_aggregator.domain.entity.User;
import com.example.tournament_aggregator.domain.entity.UserSubscription;
import com.example.tournament_aggregator.domain.repository.TeamRepository;
import com.example.tournament_aggregator.domain.repository.UserRepository;
import com.example.tournament_aggregator.domain.repository.UserSubscriptionRepository;
import com.example.tournament_aggregator.exception.ResourceNotFoundException;
import com.example.tournament_aggregator.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Override
    public UserSubscriptionResponse createUserSubscription(UserSubscriptionRequest request) {
        validate(request);
        if (userSubscriptionRepository.existsByUserIdAndTeamId(request.getUserId(), request.getTeamId())) {
            throw new IllegalArgumentException("Subscription already exists for this user and team");
        }
        UserSubscription userSubscription = UserSubscription.builder()
                .user(resolveUser(request.getUserId()))
                .team(resolveTeam(request.getTeamId()))
                .build();
        return toResponse(userSubscriptionRepository.save(userSubscription));
    }

    @Override
    public UserSubscriptionResponse updateUserSubscription(Long id, UserSubscriptionRequest request) {
        validate(request);
        UserSubscription userSubscription = getUserSubscriptionEntityById(id);
        userSubscription.setUser(resolveUser(request.getUserId()));
        userSubscription.setTeam(resolveTeam(request.getTeamId()));
        return toResponse(userSubscriptionRepository.save(userSubscription));
    }

    @Override
    @Transactional(readOnly = true)
    public UserSubscriptionResponse getUserSubscriptionById(Long id) {
        return toResponse(getUserSubscriptionEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserSubscriptionResponse> getAllUserSubscriptions() {
        return userSubscriptionRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public void deleteUserSubscription(Long id) {
        userSubscriptionRepository.delete(getUserSubscriptionEntityById(id));
    }

    private UserSubscription getUserSubscriptionEntityById(Long id) {
        return userSubscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserSubscription", id));
    }

    private User resolveUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    private Team resolveTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
    }

    private UserSubscriptionResponse toResponse(UserSubscription userSubscription) {
        return UserSubscriptionResponse.builder()
                .id(userSubscription.getId())
                .userId(userSubscription.getUser() != null ? userSubscription.getUser().getId() : null)
                .teamId(userSubscription.getTeam() != null ? userSubscription.getTeam().getId() : null)
                .subscribedAt(userSubscription.getSubscribedAt())
                .build();
    }

    private void validate(UserSubscriptionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("UserSubscription request must not be null");
        }
    }
}

