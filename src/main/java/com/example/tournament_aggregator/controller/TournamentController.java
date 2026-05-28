package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.domain.dto.tournament.TournamentRequest;
import com.example.tournament_aggregator.domain.dto.tournament.TournamentResponse;
import com.example.tournament_aggregator.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> getAll() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tournamentService.getTournamentById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<TournamentResponse> getByName(@RequestParam String name) {
        return ResponseEntity.ok(tournamentService.getTournamentByName(name));
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> create(@Valid @RequestBody TournamentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.createTournament(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody TournamentRequest request) {
        return ResponseEntity.ok(tournamentService.updateTournament(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.noContent().build();
    }
}

