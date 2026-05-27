package com.example.tournament_aggregator.service;

import com.example.tournament_aggregator.domain.entity.User;

import java.util.Map;

public interface SteamAuthService {

    String buildAuthenticationUrl(String returnTo, String realm);

    User authenticateCallback(Map<String, String> parameters);

    String extractSteamId(String claimedId);
}

