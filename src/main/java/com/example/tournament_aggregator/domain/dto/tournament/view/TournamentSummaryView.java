package com.example.tournament_aggregator.domain.dto.tournament.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentSummaryView {

    private Long id;
    private String name;
    private String description;
    private Double prizePool;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Boolean isActive;
    private long teamCount;
    private long matchCount;
}

