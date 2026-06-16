package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.domain.dto.MatchCheckResponse;
import com.example.tournament_aggregator.exception.MatchNotFoundException;
import com.example.tournament_aggregator.integration.dto.DotaMatch;
import com.example.tournament_aggregator.integration.service.DotaApiIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/integration")
@Tag(name = "Integration API", description = "Endpoints для интеграции с внешними API")
public class IntegrationController {

    private final DotaApiIntegrationService dotaApiService;

    @Autowired
    public IntegrationController(DotaApiIntegrationService dotaApiService) {
        this.dotaApiService = dotaApiService;
    }

    @GetMapping("/dota/match/{matchId}")
    @Operation(summary = "Получить матч по id", description = "Возвращает точный матч из OpenDota API по matchId")
    public ResponseEntity<Map<String, Object>> getMatchById(@PathVariable String matchId) {
        log.info("Received request to fetch match by id: {}", matchId);

        try {
            Long parsedMatchId = Long.valueOf(matchId);
            MatchCheckResponse match = dotaApiService.fetchMatchById(parsedMatchId);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("match", match);
            return ResponseEntity.ok(response);

        } catch (NumberFormatException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Match id must be a valid number");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (IllegalArgumentException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (MatchNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "not_found");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            log.error("Error fetching match by id", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/health/dota-api")
    @Operation(summary = "Проверить здоровье Dota API", description = "Проверяет, доступен ли OpenDota API")
    public ResponseEntity<Map<String, String>> checkDotaApiHealth() {
        log.info("Checking Dota API health");

        try {
            Map<String, String> response = new HashMap<>();
            response.put("status", "healthy");
            response.put("api", "OpenDota");
            response.put("message", "API is accessible");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Dota API is not accessible", e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "unhealthy");
            errorResponse.put("api", "OpenDota");
            errorResponse.put("message", "API is not accessible: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
        }
    }
}

