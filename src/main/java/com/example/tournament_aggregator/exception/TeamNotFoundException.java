package com.example.tournament_aggregator.exception;

public class TeamNotFoundException extends RuntimeException {

    public TeamNotFoundException(Long id) {
        super("Team with id %d not found".formatted(id));
    }
}

