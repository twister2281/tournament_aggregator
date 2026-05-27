package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.match.MatchRequest;
import com.example.tournament_aggregator.domain.dto.match.MatchResponse;
import com.example.tournament_aggregator.domain.entity.Match;
import com.example.tournament_aggregator.domain.entity.Team;
import com.example.tournament_aggregator.domain.entity.Tournament;
import com.example.tournament_aggregator.domain.repository.MatchRepository;
import com.example.tournament_aggregator.domain.repository.TeamRepository;
import com.example.tournament_aggregator.domain.repository.TournamentRepository;
import com.example.tournament_aggregator.exception.ResourceNotFoundException;
import com.example.tournament_aggregator.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;

    @Override
    public MatchResponse createMatch(MatchRequest request) {
        validate(request);
        Match match = Match.builder()
                .tournament(resolveTournament(request.getTournamentId()))
                .team1(resolveTeam(request.getTeam1Id()))
                .team2(resolveTeam(request.getTeam2Id()))
                .team1Score(request.getTeam1Score())
                .team2Score(request.getTeam2Score())
                .matchDate(request.getMatchDate())
                .isCompleted(request.getIsCompleted() != null && request.getIsCompleted())
                .winnerId(request.getWinnerId())
                .build();
        return toResponse(matchRepository.save(match));
    }

    @Override
    public MatchResponse updateMatch(Long id, MatchRequest request) {
        validate(request);
        Match match = getMatchEntityById(id);
        match.setTournament(resolveTournament(request.getTournamentId()));
        match.setTeam1(resolveTeam(request.getTeam1Id()));
        match.setTeam2(resolveTeam(request.getTeam2Id()));
        match.setTeam1Score(request.getTeam1Score());
        match.setTeam2Score(request.getTeam2Score());
        match.setMatchDate(request.getMatchDate());
        if (request.getIsCompleted() != null) {
            match.setIsCompleted(request.getIsCompleted());
        }
        match.setWinnerId(request.getWinnerId());
        return toResponse(matchRepository.save(match));
    }

    @Override
    @Transactional(readOnly = true)
    public MatchResponse getMatchById(Long id) {
        return toResponse(getMatchEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponse> getAllMatches() {
        return matchRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public void deleteMatch(Long id) {
        matchRepository.delete(getMatchEntityById(id));
    }

    private Match getMatchEntityById(Long id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
    }

    private Tournament resolveTournament(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));
    }

    private Team resolveTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team", id));
    }

    private MatchResponse toResponse(Match match) {
        return MatchResponse.builder()
                .id(match.getId())
                .tournamentId(match.getTournament() != null ? match.getTournament().getId() : null)
                .team1Id(match.getTeam1() != null ? match.getTeam1().getId() : null)
                .team2Id(match.getTeam2() != null ? match.getTeam2().getId() : null)
                .team1Score(match.getTeam1Score())
                .team2Score(match.getTeam2Score())
                .matchDate(match.getMatchDate())
                .isCompleted(match.getIsCompleted())
                .winnerId(match.getWinnerId())
                .createdAt(match.getCreatedAt())
                .updatedAt(match.getUpdatedAt())
                .build();
    }

    private void validate(MatchRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Match request must not be null");
        }
    }
}

