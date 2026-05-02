package com.example.tournament_aggregator.mapper;

import com.example.tournament_aggregator.domain.dto.TeamCreateRequest;
import com.example.tournament_aggregator.domain.dto.TeamResponse;
import com.example.tournament_aggregator.domain.dto.TeamUpdateRequest;
import com.example.tournament_aggregator.domain.entity.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    public Team toEntity(TeamCreateRequest request) {
        return Team.builder()
                .name(request.getName())
                .tag(request.getTag())
                .logoUrl(request.getLogoUrl())
                .description(request.getDescription())
                .winRate(request.getWinRate() != null ? request.getWinRate() : 0.0)
                .totalMatches(request.getTotalMatches() != null ? request.getTotalMatches() : 0)
                .build();
    }

    public void updateEntity(Team target, TeamUpdateRequest request) {
        if (request.getName() != null) {
            target.setName(request.getName());
        }
        if (request.getTag() != null) {
            target.setTag(request.getTag());
        }
        if (request.getLogoUrl() != null) {
            target.setLogoUrl(request.getLogoUrl());
        }
        if (request.getDescription() != null) {
            target.setDescription(request.getDescription());
        }
        if (request.getWinRate() != null) {
            target.setWinRate(request.getWinRate());
        }
        if (request.getTotalMatches() != null) {
            target.setTotalMatches(request.getTotalMatches());
        }
    }

    public TeamResponse toResponse(Team team) {
        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .tag(team.getTag())
                .logoUrl(team.getLogoUrl())
                .description(team.getDescription())
                .winRate(team.getWinRate())
                .totalMatches(team.getTotalMatches())
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }
}
