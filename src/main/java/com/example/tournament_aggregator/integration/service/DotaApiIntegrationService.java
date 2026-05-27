package com.example.tournament_aggregator.integration.service;

import com.example.tournament_aggregator.domain.entity.Team;
import com.example.tournament_aggregator.domain.repository.TeamRepository;
import com.example.tournament_aggregator.domain.dto.MatchCheckResponse;
import com.example.tournament_aggregator.integration.dto.DotaMatch;
import com.example.tournament_aggregator.integration.dto.DotaTeam;
import com.example.tournament_aggregator.integration.dto.OpenDotaMatchDetails;
import com.example.tournament_aggregator.exception.MatchNotFoundException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DotaApiIntegrationService {

    private static final String OPENDOTA_API_URL = "https://api.opendota.com/api";
    private static final String PRO_TEAMS_ENDPOINT = OPENDOTA_API_URL + "/teams";
    private static final String PRO_MATCHES_ENDPOINT = OPENDOTA_API_URL + "/proMatches";
    private static final String MATCH_DETAILS_ENDPOINT = OPENDOTA_API_URL + "/matches";

    private final OkHttpClient okHttpClient;
    private final Gson gson;
    private final TeamRepository teamRepository;

    @Autowired
    public DotaApiIntegrationService(OkHttpClient okHttpClient, TeamRepository teamRepository) {
        this.okHttpClient = okHttpClient;
        this.teamRepository = teamRepository;
        this.gson = new Gson();
    }

    /**
     * Синхронизирует команды из OpenDota API с нашей БД
     */
    @Transactional
    public void syncTeamsFromDotaApi() {
        log.info("Starting sync of teams from Dota API");

        try {
            List<DotaTeam> dotaTeams = fetchProTeams();
            log.info("Fetched {} teams from OpenDota API", dotaTeams.size());

            for (DotaTeam dotaTeam : dotaTeams) {
                syncTeam(dotaTeam);
            }

            log.info("Successfully synced {} teams", dotaTeams.size());
        } catch (Exception e) {
            log.error("Error syncing teams from Dota API", e);
            throw new RuntimeException("Failed to sync teams from external API", e);
        }
    }

    /**
     * Получает про-матчи из OpenDota API
     */
    public List<DotaMatch> fetchProMatches(Long matchId) {
        log.info("Fetching pro matches with matchId less than: {}", matchId);

        try {
            String url;
            if (matchId == null || matchId <= 0) {
                url = PRO_MATCHES_ENDPOINT;
            } else {
                url = PRO_MATCHES_ENDPOINT + "?less_than_match_id=" + matchId;
            }
            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "TournamentAggregator/1.0")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code: " + response.code());
                }

                String responseBody = response.body().string();
                Type listType = new TypeToken<List<DotaMatch>>(){}.getType();
                List<DotaMatch> matches = gson.fromJson(responseBody, listType);

                log.info("Successfully fetched {} matches from Dota API", matches.size());
                return matches;
            }
        } catch (IOException e) {
            log.error("Error fetching matches from Dota API", e);
            return List.of();
        }
    }

    /**
     * Получает один матч по точному matchId
     */
    public MatchCheckResponse fetchMatchById(Long matchId) {
        if (matchId == null || matchId <= 0) {
            throw new IllegalArgumentException("Match id must be positive");
        }

        log.info("Fetching exact match by id: {}", matchId);

        try {
            Request request = new Request.Builder()
                    .url(MATCH_DETAILS_ENDPOINT + "/" + matchId)
                    .header("User-Agent", "TournamentAggregator/1.0")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (response.code() == 404) {
                    throw new MatchNotFoundException(matchId);
                }

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code: " + response.code());
                }

                String responseBody = response.body().string();
                OpenDotaMatchDetails matchDetails = gson.fromJson(responseBody, OpenDotaMatchDetails.class);
                if (matchDetails == null || matchDetails.getMatchId() == null) {
                    throw new MatchNotFoundException(matchId);
                }

                return toMatchCheckResponse(matchDetails);
            }
        } catch (IOException e) {
            log.error("Error fetching exact match from Dota API", e);
            throw new RuntimeException("Failed to fetch match details from external API", e);
        }
    }

    /**
     * Получает список всех про-команд из OpenDota
     */
    public List<DotaTeam> fetchProTeams() {
        log.info("Fetching pro teams from OpenDota API");

        try {
            Request request = new Request.Builder()
                    .url(PRO_TEAMS_ENDPOINT)
                    .header("User-Agent", "TournamentAggregator/1.0")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code: " + response.code());
                }

                String responseBody = response.body().string();
                Type listType = new TypeToken<List<DotaTeam>>(){}.getType();
                List<DotaTeam> teams = gson.fromJson(responseBody, listType);

                log.info("Successfully fetched {} teams from Dota API", teams.size());
                return teams;
            }
        } catch (IOException e) {
            log.error("Error fetching teams from Dota API", e);
            return List.of();
        }
    }

    /**
     * Синхронизирует одну команду: создает или обновляет
     */
    private void syncTeam(DotaTeam dotaTeam) {
        try {
            Optional<Team> existingTeam = teamRepository.findByName(dotaTeam.getName());

            Team team;
            if (existingTeam.isPresent()) {
                team = existingTeam.get();
                log.debug("Updating existing team: {}", dotaTeam.getName());
            } else {
                team = new Team();
                log.debug("Creating new team: {}", dotaTeam.getName());
            }

            // Обновляем данные команды
            team.setName(dotaTeam.getName());
            team.setTag(dotaTeam.getTag() != null ? dotaTeam.getTag() : "");
            team.setLogoUrl(dotaTeam.getLogoUrl());

            // Вычисляем win rate
            int totalMatches = (dotaTeam.getWins() != null ? dotaTeam.getWins() : 0) +
                              (dotaTeam.getLosses() != null ? dotaTeam.getLosses() : 0);
            double winRate = totalMatches > 0 ?
                (double) (dotaTeam.getWins() != null ? dotaTeam.getWins() : 0) / totalMatches : 0.0;

            team.setWinRate(winRate);
            team.setTotalMatches(totalMatches);
            team.setDescription("Professional Dota 2 team from OpenDota");

            teamRepository.save(team);
            log.info("Successfully synced team: {}", dotaTeam.getName());
        } catch (Exception e) {
            log.error("Error syncing team: {}", dotaTeam.getName(), e);
        }
    }

    /**
     * Получает команду по имени
     */
    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByName(name);
    }

    /**
     * Находит team_id по имени команды, делая поиск по списку про-команд из OpenDota
     */
    public Long findTeamIdByName(String name) {
        if (name == null || name.isBlank()) return null;

        List<DotaTeam> teams = fetchProTeams();
        String lower = name.toLowerCase().trim();
        for (DotaTeam t : teams) {
            if (t == null) continue;
            String teamName = t.getName() != null ? t.getName().toLowerCase() : "";
            String tag = t.getTag() != null ? t.getTag().toLowerCase() : "";
            if (teamName.equals(lower) || teamName.contains(lower) || tag.equals(lower) || tag.contains(lower)) {
                return t.getTeamId();
            }
        }
        return null;
    }

    /**
     * Получает матчи конкретной команды по teamId
     */
    public List<DotaMatch> fetchMatchesForTeam(Long teamId, int limit) {
        log.info("Fetching matches for team {} with limit {}", teamId, limit);

        if (teamId == null) return List.of();

        try {
            String url = OPENDOTA_API_URL + "/teams/" + teamId + "/matches?limit=" + Math.max(1, limit);
            Request request = new Request.Builder()
                    .url(url)
                    .header("User-Agent", "TournamentAggregator/1.0")
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code: " + response.code());
                }

                String responseBody = response.body().string();
                Type listType = new TypeToken<List<DotaMatch>>(){}.getType();
                List<DotaMatch> matches = gson.fromJson(responseBody, listType);

                log.info("Successfully fetched {} team matches from Dota API", matches.size());
                return matches;
            }
        } catch (IOException e) {
            log.error("Error fetching team matches from Dota API", e);
            return List.of();
        }
    }

    private MatchCheckResponse toMatchCheckResponse(OpenDotaMatchDetails matchDetails) {
        String radiantName = normalizeTeamName(matchDetails.getRadiantName(), "Radiant");
        String direName = normalizeTeamName(matchDetails.getDireName(), "Dire");
        Boolean radiantWin = matchDetails.getRadiantWin();
        String winnerTeamName = null;

        if (radiantWin != null) {
            winnerTeamName = Boolean.TRUE.equals(radiantWin) ? radiantName : direName;
        }

        return MatchCheckResponse.builder()
                .matchId(matchDetails.getMatchId())
                .radiantTeamName(radiantName)
                .direTeamName(direName)
                .durationSeconds(matchDetails.getDuration())
                .winnerTeamName(winnerTeamName)
                .build();
    }

    private String normalizeTeamName(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value.trim();
    }
}



