package com.example.tournament_aggregator.integration.service;

import com.example.tournament_aggregator.domain.entity.Team;
import com.example.tournament_aggregator.domain.repository.TeamRepository;
import com.example.tournament_aggregator.domain.dto.MatchCheckResponse;
import com.example.tournament_aggregator.integration.dto.DotaMatch;
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





    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByName(name);
    }


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
            winnerTeamName = radiantWin ? radiantName : direName;
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



