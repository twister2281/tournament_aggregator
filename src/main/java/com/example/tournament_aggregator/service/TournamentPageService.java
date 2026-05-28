package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.dto.tournament.view.TournamentDetailView;
import com.example.tournament_aggregator.domain.dto.tournament.view.TournamentSummaryView;

import java.util.List;

public interface TournamentPageService {

    List<TournamentSummaryView> getTournamentSummaries();

    TournamentDetailView getTournamentDetail(Long id);
}

