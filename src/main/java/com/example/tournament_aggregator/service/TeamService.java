package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.dto.TeamCreateRequest;
import com.example.tournament_aggregator.domain.dto.TeamResponse;
import com.example.tournament_aggregator.domain.dto.TeamUpdateRequest;

import java.util.List;

public interface TeamService {

    TeamResponse createTeam(TeamCreateRequest request);

    TeamResponse updateTeam(Long id, TeamUpdateRequest request);

    TeamResponse getTeamById(Long id);

    List<TeamResponse> getAllTeams();

    void deleteTeam(Long id);
}
