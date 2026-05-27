package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.domain.dto.match.MatchRequest;
import com.example.tournament_aggregator.domain.dto.match.MatchResponse;
import com.example.tournament_aggregator.service.MatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<List<MatchResponse>> getAll() {
        return ResponseEntity.ok(matchService.getAllMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    @PostMapping
    public ResponseEntity<MatchResponse> create(@Valid @RequestBody MatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.createMatch(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MatchResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody MatchRequest request) {
        return ResponseEntity.ok(matchService.updateMatch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }
}

