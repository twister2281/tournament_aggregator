package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.dto.playerstatistic.PlayerStatisticRequest;
import com.example.tournament_aggregator.domain.dto.playerstatistic.PlayerStatisticResponse;

import java.util.List;

public interface PlayerStatisticService {

    PlayerStatisticResponse createPlayerStatistic(PlayerStatisticRequest request);

    PlayerStatisticResponse updatePlayerStatistic(Long id, PlayerStatisticRequest request);

    PlayerStatisticResponse getPlayerStatisticById(Long id);

    List<PlayerStatisticResponse> getAllPlayerStatistics();

    void deletePlayerStatistic(Long id);
}

