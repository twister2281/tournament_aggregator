package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.playerstatistic.PlayerStatisticRequest;
import com.example.tournament_aggregator.domain.dto.playerstatistic.PlayerStatisticResponse;
import com.example.tournament_aggregator.domain.entity.Match;
import com.example.tournament_aggregator.domain.entity.PlayerStatistic;
import com.example.tournament_aggregator.domain.entity.Team;
import com.example.tournament_aggregator.domain.repository.MatchRepository;
import com.example.tournament_aggregator.domain.repository.PlayerStatisticRepository;
import com.example.tournament_aggregator.domain.repository.TeamRepository;
import com.example.tournament_aggregator.exception.ResourceNotFoundException;
import com.example.tournament_aggregator.service.PlayerStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerStatisticServiceImpl implements PlayerStatisticService {

    private final PlayerStatisticRepository playerStatisticRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    @Override
    public PlayerStatisticResponse createPlayerStatistic(PlayerStatisticRequest request) {
        validate(request);
        PlayerStatistic playerStatistic = PlayerStatistic.builder()
                .match(resolveMatch(request.getMatchId()))
                .team(resolveTeam(request.getTeamId()))
                .playerName(request.getPlayerName().trim())
                .heroName(normalize(request.getHeroName()))
                .kills(defaultZero(request.getKills()))
                .deaths(defaultZero(request.getDeaths()))
                .assists(defaultZero(request.getAssists()))
                .lastHits(defaultZero(request.getLastHits()))
                .denies(defaultZero(request.getDenies()))
                .build();
        return toResponse(playerStatisticRepository.save(playerStatistic));
    }

    @Override
    public PlayerStatisticResponse updatePlayerStatistic(Long id, PlayerStatisticRequest request) {
        validate(request);
        PlayerStatistic playerStatistic = getPlayerStatisticEntityById(id);
        playerStatistic.setMatch(resolveMatch(request.getMatchId()));
        playerStatistic.setTeam(resolveTeam(request.getTeamId()));
        playerStatistic.setPlayerName(request.getPlayerName().trim());
        playerStatistic.setHeroName(normalize(request.getHeroName()));
        playerStatistic.setKills(defaultZero(request.getKills()));
        playerStatistic.setDeaths(defaultZero(request.getDeaths()));
        playerStatistic.setAssists(defaultZero(request.getAssists()));
        playerStatistic.setLastHits(defaultZero(request.getLastHits()));
        playerStatistic.setDenies(defaultZero(request.getDenies()));
        return toResponse(playerStatisticRepository.save(playerStatistic));
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerStatisticResponse getPlayerStatisticById(Long id) {
        return toResponse(getPlayerStatisticEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerStatisticResponse> getAllPlayerStatistics() {
        return playerStatisticRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public void deletePlayerStatistic(Long id) {
        playerStatisticRepository.delete(getPlayerStatisticEntityById(id));
    }

    private PlayerStatistic getPlayerStatisticEntityById(Long id) {
        return playerStatisticRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlayerStatistic", id));
    }

    private Match resolveMatch(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
    }

    private Team resolveTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
    }

    private PlayerStatisticResponse toResponse(PlayerStatistic playerStatistic) {
        return PlayerStatisticResponse.builder()
                .id(playerStatistic.getId())
                .matchId(playerStatistic.getMatch() != null ? playerStatistic.getMatch().getId() : null)
                .teamId(playerStatistic.getTeam() != null ? playerStatistic.getTeam().getId() : null)
                .playerName(playerStatistic.getPlayerName())
                .heroName(playerStatistic.getHeroName())
                .kills(playerStatistic.getKills())
                .deaths(playerStatistic.getDeaths())
                .assists(playerStatistic.getAssists())
                .lastHits(playerStatistic.getLastHits())
                .denies(playerStatistic.getDenies())
                .createdAt(playerStatistic.getCreatedAt())
                .updatedAt(playerStatistic.getUpdatedAt())
                .build();
    }

    private void validate(PlayerStatisticRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("PlayerStatistic request must not be null");
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }
}

