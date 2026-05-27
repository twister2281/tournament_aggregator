package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.domain.dto.prediction.PredictionRequest;
import com.example.tournament_aggregator.domain.dto.prediction.PredictionResponse;
import com.example.tournament_aggregator.service.PredictionService;
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
@RequestMapping("/api/predictions")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;

    @GetMapping
    public ResponseEntity<List<PredictionResponse>> getAll() {
        return ResponseEntity.ok(predictionService.getAllPredictions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PredictionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(predictionService.getPredictionById(id));
    }

    @PostMapping
    public ResponseEntity<PredictionResponse> create(@Valid @RequestBody PredictionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(predictionService.createPrediction(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PredictionResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody PredictionRequest request) {
        return ResponseEntity.ok(predictionService.updatePrediction(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        predictionService.deletePrediction(id);
        return ResponseEntity.noContent().build();
    }
}

