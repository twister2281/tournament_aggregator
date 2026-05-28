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
public class TournamentBracketMatchView {

    private Long id;
    private Integer roundNumber;
    private TournamentTeamView team1;
    private TournamentTeamView team2;
    private Integer team1Score;
    private Integer team2Score;
    private Long winnerId;
    private String winnerName;
    private Boolean isCompleted;
    private LocalDateTime matchDate;
}

