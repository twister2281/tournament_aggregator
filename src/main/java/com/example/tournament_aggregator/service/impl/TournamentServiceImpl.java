package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.tournament.TournamentRequest;
import com.example.tournament_aggregator.domain.dto.tournament.TournamentResponse;
import com.example.tournament_aggregator.domain.entity.Match;
import com.example.tournament_aggregator.domain.entity.Team;
import com.example.tournament_aggregator.domain.entity.Tournament;
import com.example.tournament_aggregator.domain.repository.TeamRepository;
import com.example.tournament_aggregator.domain.repository.TournamentRepository;
import com.example.tournament_aggregator.exception.ResourceNotFoundException;
import com.example.tournament_aggregator.exception.TournamentNotFoundException;
import com.example.tournament_aggregator.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TeamRepository teamRepository;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TournamentResponse createTournament(TournamentRequest request) {
        validate(request);
        ensureNameAvailable(request.getName(), null);
        Tournament tournament = Tournament.builder()
                .name(request.getName().trim())
                .description(normalize(request.getDescription()))
                .prizePool(request.getPrizePool())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(normalize(request.getLocation()))
                .isActive(request.getIsActive() == null || request.getIsActive())
                .teams(resolveTeams(request.getTeamIds()))
                .build();
        return toResponse(tournamentRepository.save(tournament));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public TournamentResponse updateTournament(Long id, TournamentRequest request) {
        validate(request);
        Tournament tournament = getTournamentEntityById(id);
        ensureNameAvailable(request.getName(), id);
        tournament.setName(request.getName().trim());
        tournament.setDescription(normalize(request.getDescription()));
        tournament.setPrizePool(request.getPrizePool());
        tournament.setStartDate(request.getStartDate());
        tournament.setEndDate(request.getEndDate());
        tournament.setLocation(normalize(request.getLocation()));
        if (request.getIsActive() != null) {
            tournament.setIsActive(request.getIsActive());
        }
        if (request.getTeamIds() != null) {
            tournament.setTeams(resolveTeams(request.getTeamIds()));
        }
        return toResponse(tournamentRepository.save(tournament));
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentResponse getTournamentById(Long id) {
        return toResponse(getTournamentEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentResponse getTournamentByName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tournament name must not be blank");
        }
        Tournament tournament = tournamentRepository.findByName(name.trim())
                .orElseThrow(() -> new TournamentNotFoundException(name.trim()));
        return toResponse(tournament);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentResponse> getAllTournaments() {
        return tournamentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTournament(Long id) {
        Tournament tournament = getTournamentEntityById(id);
        tournamentRepository.delete(tournament);
    }

    private Tournament getTournamentEntityById(Long id) {
        return tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", id));
    }

    private Set<Team> resolveTeams(Set<Long> teamIds) {
        if (teamIds == null || teamIds.isEmpty()) {
            return Set.of();
        }
        return teamIds.stream()
                .map(teamId -> teamRepository.findById(teamId)
                        .orElseThrow(() -> new ResourceNotFoundException("Team", teamId)))
                .collect(Collectors.toSet());
    }

    private TournamentResponse toResponse(Tournament tournament) {
        return TournamentResponse.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .description(tournament.getDescription())
                .prizePool(tournament.getPrizePool())
                .startDate(tournament.getStartDate())
                .endDate(tournament.getEndDate())
                .location(tournament.getLocation())
                .isActive(tournament.getIsActive())
                .teamIds(tournament.getTeams() == null ? Set.of() : tournament.getTeams().stream().map(Team::getId).collect(Collectors.toSet()))
                .matchIds(tournament.getMatches() == null ? Set.of() : tournament.getMatches().stream().map(Match::getId).collect(Collectors.toSet()))
                .createdAt(tournament.getCreatedAt())
                .updatedAt(tournament.getUpdatedAt())
                .build();
    }

    private void validate(TournamentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Tournament request must not be null");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Tournament name must not be blank");
        }
    }

    private void ensureNameAvailable(String name, Long currentTournamentId) {
        tournamentRepository.findByName(name.trim())
                .filter(tournament -> !Objects.equals(tournament.getId(), currentTournamentId))
                .ifPresent(tournament -> {
                    throw new IllegalArgumentException("Tournament name is already taken");
                });
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

