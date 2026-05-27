package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.dto.match.MatchRequest;
import com.example.tournament_aggregator.domain.dto.match.MatchResponse;

import java.util.List;

public interface MatchService {

    MatchResponse createMatch(MatchRequest request);

    MatchResponse updateMatch(Long id, MatchRequest request);

    MatchResponse getMatchById(Long id);

    List<MatchResponse> getAllMatches();

    void deleteMatch(Long id);
}

