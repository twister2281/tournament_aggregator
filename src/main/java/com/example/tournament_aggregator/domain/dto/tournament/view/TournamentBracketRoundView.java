package com.example.tournament_aggregator.domain.dto.tournament.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentBracketRoundView {

    private Integer roundNumber;
    private String title;
    private List<TournamentBracketMatchView> matches;
}

