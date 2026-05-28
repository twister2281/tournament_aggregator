package com.example.tournament_aggregator.domain.dto.tournament.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentTeamView {

    private Long id;
    private String name;
    private String tag;
    private String logoUrl;
    private Double winRate;
    private Integer totalMatches;
}

