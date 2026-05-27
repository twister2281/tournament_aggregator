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

    /**
     * Ищет team_id по имени команды через OpenDota и возвращает его (через локальный endpoint)
     */
    @GetMapping("/dota/team/search")
    @Operation(summary = "Найти team_id по имени", description = "Ищет team_id команды по имени через OpenDota и возвращает его")
    public ResponseEntity<Map<String, Object>> findTeamId(@RequestParam(defaultValue = "Team Spirit") String name) {
        log.info("Received request to find team id for name: {}", name);

        try {
            Long teamId = dotaApiService.findTeamIdByName(name);
            Map<String, Object> response = new HashMap<>();
            if (teamId == null) {
                response.put("status", "not_found");
                response.put("teamId", null);
                response.put("message", "Team not found: " + name);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("status", "success");
            response.put("teamId", teamId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error finding team id", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Получает точный матч по matchId через OpenDota API
     */
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

    /**
     * Синхронизирует все про-команды из OpenDota API
     */
    @PostMapping("/sync/teams")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Синхронизировать команды", description = "Загружает все про-команды из OpenDota API")
    public ResponseEntity<Map<String, String>> syncTeams() {
        log.info("Received request to sync teams from Dota API");

        try {
            dotaApiService.syncTeamsFromDotaApi();

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Teams synchronized successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during team synchronization", e);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Получает про-матчи из OpenDota API
     */
    @GetMapping("/dota/matches")
    @Operation(summary = "Получить про-матчи", description = "Возвращает список про-матчей из OpenDota API")
    public ResponseEntity<Map<String, Object>> getProMatches(
            @RequestParam(defaultValue = "0") Long matchId,
            @RequestParam(required = false) Integer limit) {

        log.info("Received request to fetch pro matches with matchId: {}", matchId);

        try {
            List<DotaMatch> matches = dotaApiService.fetchProMatches(matchId);

            // If caller requested a limit, trim the list to that size
            if (limit != null && limit > 0 && matches.size() > limit) {
                matches = matches.subList(0, Math.min(limit, matches.size()));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", matches.size());
            response.put("matches", matches);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching pro matches", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Возвращает последний матч для команды по имени (через OpenDota -> локальный endpoint)
     */
    @GetMapping("/dota/team/latest")
    @Operation(summary = "Последний матч команды", description = "Возвращает последний про-матч для команды по имени через OpenDota")
    public ResponseEntity<Map<String, Object>> getLatestTeamMatch(
            @RequestParam(required = false, defaultValue = "Team Spirit") String name,
            @RequestParam(required = false) Long teamId) {
        log.info("Received request to fetch latest match for team: name='{}' teamId={}", name, teamId);

        try {
            Long effectiveTeamId = teamId;
            if (effectiveTeamId == null) {
                effectiveTeamId = dotaApiService.findTeamIdByName(name);
            }

            if (effectiveTeamId == null) {
                Map<String, Object> resp = new HashMap<>();
                resp.put("status", "error");
                resp.put("message", "Team not found: " + name + ", and no teamId provided");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
            }

            List<DotaMatch> matches = dotaApiService.fetchMatchesForTeam(effectiveTeamId, 1);
            if (name != null && !name.isBlank()) {
                matches.forEach(match -> {
                    if (match != null && (match.getTeamName() == null || match.getTeamName().isBlank())) {
                        match.setTeamName(name);
                    }
                });
            }

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", matches.size());
            response.put("matches", matches);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching latest team match", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Проверяет доступность Dota API
     */
    @GetMapping("/health/dota-api")
    @Operation(summary = "Проверить здоровье Dota API", description = "Проверяет, доступен ли OpenDota API")
    public ResponseEntity<Map<String, String>> checkDotaApiHealth() {
        log.info("Checking Dota API health");

        try {
            dotaApiService.fetchProMatches(0L);

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

