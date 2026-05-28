package com.example.tournament_aggregator.domain.dto.tournament.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentDetailView {

    private Long id;
    private String name;
    private String description;
    private Double prizePool;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Boolean isActive;
    private List<TournamentTeamView> teams;
    private List<TournamentBracketRoundView> rounds;
    private long matchCount;
    private long teamCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

