package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.tournament.view.TournamentBracketMatchView;
import com.example.tournament_aggregator.domain.dto.tournament.view.TournamentBracketRoundView;
import com.example.tournament_aggregator.domain.dto.tournament.view.TournamentDetailView;
import com.example.tournament_aggregator.domain.dto.tournament.view.TournamentSummaryView;
import com.example.tournament_aggregator.domain.dto.tournament.view.TournamentTeamView;
import com.example.tournament_aggregator.domain.entity.Match;
import com.example.tournament_aggregator.domain.entity.Team;
import com.example.tournament_aggregator.domain.entity.Tournament;
import com.example.tournament_aggregator.domain.repository.TournamentRepository;
import com.example.tournament_aggregator.exception.ResourceNotFoundException;
import com.example.tournament_aggregator.service.TournamentPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TournamentPageServiceImpl implements TournamentPageService {

    private final TournamentRepository tournamentRepository;

    @Override
    public List<TournamentSummaryView> getTournamentSummaries() {
        return tournamentRepository.findAll().stream()
                .sorted(Comparator.comparing(Tournament::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toSummaryView)
                .toList();
    }

    @Override
    public TournamentDetailView getTournamentDetail(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));
        return toDetailView(tournament);
    }

    private TournamentSummaryView toSummaryView(Tournament tournament) {
        return TournamentSummaryView.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .description(tournament.getDescription())
                .prizePool(tournament.getPrizePool())
                .startDate(tournament.getStartDate())
                .endDate(tournament.getEndDate())
                .location(tournament.getLocation())
                .isActive(tournament.getIsActive())
                .teamCount(tournament.getTeams() == null ? 0 : tournament.getTeams().size())
                .matchCount(tournament.getMatches() == null ? 0 : tournament.getMatches().size())
                .build();
    }

    private TournamentDetailView toDetailView(Tournament tournament) {
        List<TournamentTeamView> teams = tournament.getTeams() == null ? List.of() : tournament.getTeams().stream()
                .sorted(Comparator.comparing(Team::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(this::toTeamView)
                .toList();

        List<TournamentBracketRoundView> rounds = buildRounds(tournament);

        return TournamentDetailView.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .description(tournament.getDescription())
                .prizePool(tournament.getPrizePool())
                .startDate(tournament.getStartDate())
                .endDate(tournament.getEndDate())
                .location(tournament.getLocation())
                .isActive(tournament.getIsActive())
                .teams(teams)
                .rounds(rounds)
                .matchCount(tournament.getMatches() == null ? 0 : tournament.getMatches().size())
                .teamCount(tournament.getTeams() == null ? 0 : tournament.getTeams().size())
                .createdAt(tournament.getCreatedAt())
                .updatedAt(tournament.getUpdatedAt())
                .build();
    }

    private List<TournamentBracketRoundView> buildRounds(Tournament tournament) {
        if (tournament.getMatches() == null || tournament.getMatches().isEmpty()) {
            return List.of();
        }

        Map<Integer, List<Match>> grouped = tournament.getMatches().stream()
                .sorted(Comparator
                        .comparing((Match match) -> defaultRound(match.getRoundNumber()))
                        .thenComparing(Match::getMatchDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Match::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.groupingBy(
                        match -> defaultRound(match.getRoundNumber()),
                        LinkedHashMap::new,
                        Collectors.toList()));

        return grouped.entrySet().stream()
                .map(entry -> TournamentBracketRoundView.builder()
                        .roundNumber(entry.getKey())
                        .title(buildRoundTitle(entry.getKey()))
                        .matches(entry.getValue().stream().map(this::toBracketMatchView).toList())
                        .build())
                .toList();
    }

    private TournamentBracketMatchView toBracketMatchView(Match match) {
        TournamentTeamView team1 = toTeamView(match.getTeam1());
        TournamentTeamView team2 = toTeamView(match.getTeam2());
        String winnerName = null;
        if (match.getWinnerId() != null) {
            if (team1 != null && match.getWinnerId().equals(team1.getId())) {
                winnerName = team1.getName();
            } else if (team2 != null && match.getWinnerId().equals(team2.getId())) {
                winnerName = team2.getName();
            }
        }

        return TournamentBracketMatchView.builder()
                .id(match.getId())
                .roundNumber(defaultRound(match.getRoundNumber()))
                .team1(team1)
                .team2(team2)
                .team1Score(match.getTeam1Score())
                .team2Score(match.getTeam2Score())
                .winnerId(match.getWinnerId())
                .winnerName(winnerName)
                .isCompleted(match.getIsCompleted())
                .matchDate(match.getMatchDate())
                .build();
    }

    private TournamentTeamView toTeamView(Team team) {
        if (team == null) {
            return null;
        }
        return TournamentTeamView.builder()
                .id(team.getId())
                .name(team.getName())
                .tag(team.getTag())
                .logoUrl(team.getLogoUrl())
                .winRate(team.getWinRate())
                .totalMatches(team.getTotalMatches())
                .build();
    }

    private int defaultRound(Integer roundNumber) {
        return roundNumber == null || roundNumber < 1 ? 1 : roundNumber;
    }

    private String buildRoundTitle(int roundNumber) {
        return "Раунд " + roundNumber;
    }
}


