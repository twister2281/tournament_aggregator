package com.example.tournament_aggregator.exception;

public class MatchNotFoundException extends RuntimeException {

    public MatchNotFoundException(Long matchId) {
        super("Match with id %d not found".formatted(matchId));
    }
}

