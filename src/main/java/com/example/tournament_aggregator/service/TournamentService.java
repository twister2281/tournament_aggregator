package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.dto.tournament.TournamentRequest;
import com.example.tournament_aggregator.domain.dto.tournament.TournamentResponse;

import java.util.List;

public interface TournamentService {

    TournamentResponse createTournament(TournamentRequest request);

    TournamentResponse updateTournament(Long id, TournamentRequest request);

    TournamentResponse getTournamentById(Long id);

    List<TournamentResponse> getAllTournaments();

    void deleteTournament(Long id);
}

