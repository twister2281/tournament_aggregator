package com.example.tournament_aggregator.exception;

public class TournamentNotFoundException extends RuntimeException {

    public TournamentNotFoundException(String name) {
        super("Tournament not found with name: " + name);
    }
}

