package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.TeamResponse;
import com.example.tournament_aggregator.domain.dto.subscription.UserSubscriptionRequest;
import com.example.tournament_aggregator.domain.dto.subscription.UserSubscriptionResponse;
import com.example.tournament_aggregator.domain.dto.subscription.view.SubscriptionItemView;
import com.example.tournament_aggregator.domain.dto.team.view.TeamCardView;
import com.example.tournament_aggregator.domain.entity.Team;
import com.example.tournament_aggregator.domain.entity.User;
import com.example.tournament_aggregator.domain.entity.UserSubscription;
import com.example.tournament_aggregator.domain.repository.TeamRepository;
import com.example.tournament_aggregator.domain.repository.UserRepository;
import com.example.tournament_aggregator.domain.repository.UserSubscriptionRepository;
import com.example.tournament_aggregator.exception.ResourceNotFoundException;
import com.example.tournament_aggregator.service.TeamService;
import com.example.tournament_aggregator.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserSubscriptionServiceImpl implements UserSubscriptionService {

    private final UserSubscriptionRepository userSubscriptionRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamService teamService;

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

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionItemView> getSubscriptionsForUser(Long userId) {
        resolveUser(userId);
        return userSubscriptionRepository.findByUser_IdOrderBySubscribedAtDesc(userId).stream()
                .map(this::toItemView)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamCardView> getTeamCardsForUser(Long userId) {
        Map<Long, UserSubscription> subscriptionsByTeamId = userId == null
                ? Map.of()
                : userSubscriptionRepository.findByUser_IdOrderBySubscribedAtDesc(userId).stream()
                .filter(subscription -> subscription.getTeam() != null)
                .collect(Collectors.toMap(
                        subscription -> subscription.getTeam().getId(),
                        subscription -> subscription,
                        (left, right) -> left
                ));

        return teamService.getAllTeams().stream()
                .sorted(Comparator.comparing(TeamResponse::getName, String.CASE_INSENSITIVE_ORDER))
                .map(team -> toTeamCardView(team, subscriptionsByTeamId.get(team.getId())))
                .toList();
    }

    @Override
    public void subscribeUserToTeam(Long userId, Long teamId) {
        if (userSubscriptionRepository.existsByUserIdAndTeamId(userId, teamId)) {
            throw new IllegalArgumentException("Вы уже подписаны на эту команду");
        }
        UserSubscription userSubscription = UserSubscription.builder()
                .user(resolveUser(userId))
                .team(resolveTeam(teamId))
                .build();
        userSubscriptionRepository.save(userSubscription);
    }

    @Override
    public void unsubscribeUserFromTeam(Long userId, Long subscriptionId) {
        UserSubscription subscription = userSubscriptionRepository.findByIdAndUser_Id(subscriptionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("UserSubscription", subscriptionId));
        userSubscriptionRepository.delete(subscription);
    }

    @Override
    public void unsubscribeUserFromTeamByTeamId(Long userId, Long teamId) {
        UserSubscription subscription = userSubscriptionRepository.findByUser_IdAndTeam_Id(userId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException("UserSubscription for team", teamId));
        userSubscriptionRepository.delete(subscription);
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

    private SubscriptionItemView toItemView(UserSubscription subscription) {
        Team team = subscription.getTeam();
        return SubscriptionItemView.builder()
                .subscriptionId(subscription.getId())
                .teamId(team != null ? team.getId() : null)
                .teamName(team != null ? team.getName() : "—")
                .teamTag(team != null ? team.getTag() : "")
                .logoUrl(team != null ? team.getLogoUrl() : null)
                .winRate(team != null ? team.getWinRate() : null)
                .totalMatches(team != null ? team.getTotalMatches() : null)
                .subscribedAt(subscription.getSubscribedAt())
                .build();
    }

    private TeamCardView toTeamCardView(TeamResponse team, UserSubscription subscription) {
        return TeamCardView.builder()
                .id(team.getId())
                .name(team.getName())
                .tag(team.getTag())
                .logoUrl(team.getLogoUrl())
                .description(team.getDescription())
                .winRate(team.getWinRate())
                .totalMatches(team.getTotalMatches())
                .subscribed(subscription != null)
                .subscriptionId(subscription != null ? subscription.getId() : null)
                .build();
    }
}

