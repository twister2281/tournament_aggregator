package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.dto.TeamCreateRequest;
import com.example.tournament_aggregator.domain.dto.TeamResponse;
import com.example.tournament_aggregator.domain.dto.TeamUpdateRequest;
import com.example.tournament_aggregator.domain.entity.Team;
import com.example.tournament_aggregator.domain.repository.TeamRepository;
import com.example.tournament_aggregator.exception.TeamNotFoundException;
import com.example.tournament_aggregator.mapper.TeamMapper;
import com.example.tournament_aggregator.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(cacheNames = {"teams", "tournaments", "tournamentSummaries", "tournamentDetails"}, allEntries = true)
    public TeamResponse createTeam(TeamCreateRequest request) {
        validateRequest(request);
        Team team = teamMapper.toEntity(request);
        return teamMapper.toResponse(teamRepository.save(team));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(cacheNames = {"teams", "tournaments", "tournamentSummaries", "tournamentDetails"}, allEntries = true)
    public TeamResponse updateTeam(Long id, TeamUpdateRequest request) {
        validateRequest(request);
        Team existingTeam = getTeamEntityById(id);
        teamMapper.updateEntity(existingTeam, request);
        return teamMapper.toResponse(teamRepository.save(existingTeam));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "teams", key = "#id")
    public TeamResponse getTeamById(Long id) {
        return teamMapper.toResponse(getTeamEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "teams", key = "'all'" )
    public List<TeamResponse> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(teamMapper::toResponse)
                .toList();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(cacheNames = {"teams", "tournaments", "tournamentSummaries", "tournamentDetails"}, allEntries = true)
    public void deleteTeam(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new TeamNotFoundException(id);
        }
        teamRepository.deleteById(id);
    }

    private Team getTeamEntityById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
    }

    private void validateRequest(TeamCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Team request must not be null");
        }
        validateRequest(request.getName(), request.getTag());
    }

    private void validateRequest(TeamUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Team request must not be null");
        }
        validateRequest(request.getName(), request.getTag());
    }

    private void validateRequest(String name, String tag) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Team name must not be blank");
        }
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Team tag must not be blank");
        }
    }
}
