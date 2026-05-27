package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.entity.User;
import com.example.tournament_aggregator.domain.enums.AuthProvider;
import com.example.tournament_aggregator.domain.enums.UserRole;
import com.example.tournament_aggregator.domain.repository.UserRepository;
import com.example.tournament_aggregator.service.SteamAuthService;
import lombok.RequiredArgsConstructor;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class SteamAuthServiceImpl implements SteamAuthService {

    private static final Logger log = LoggerFactory.getLogger(SteamAuthServiceImpl.class);
    private static final String STEAM_OPENID_ENDPOINT = "https://steamcommunity.com/openid/login";
    private static final String OPENID_NS = "http://specs.openid.net/auth/2.0";
    private static final String IDENTIFIER_SELECT = "http://specs.openid.net/auth/2.0/identifier_select";
    private static final Pattern STEAM_ID_PATTERN = Pattern.compile("steamcommunity\\.com/openid/id/(\\d+)");

    private final OkHttpClient okHttpClient;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String buildAuthenticationUrl(String returnTo, String realm) {
        return UriComponentsBuilder.fromUriString(STEAM_OPENID_ENDPOINT)
                .queryParam("openid.ns", OPENID_NS)
                .queryParam("openid.mode", "checkid_setup")
                .queryParam("openid.return_to", returnTo)
                .queryParam("openid.realm", realm)
                .queryParam("openid.identity", IDENTIFIER_SELECT)
                .queryParam("openid.claimed_id", IDENTIFIER_SELECT)
                .build()
                .encode()
                .toUriString();
    }

    @Override
    public User authenticateCallback(Map<String, String> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            throw new IllegalArgumentException("Steam callback parameters must not be empty");
        }

        String mode = parameters.get("openid.mode");
        if (!"id_res".equals(mode)) {
            throw new IllegalArgumentException("Steam authentication was not completed successfully");
        }

        if (!verifyAssertion(parameters)) {
            throw new IllegalArgumentException("Steam authentication could not be verified");
        }

        String claimedId = parameters.get("openid.claimed_id");
        String steamId = extractSteamId(claimedId);
        if (steamId == null) {
            steamId = extractSteamId(parameters.get("openid.identity"));
        }
        if (steamId == null) {
            throw new IllegalArgumentException("Unable to extract Steam ID from callback");
        }

        final String resolvedSteamId = steamId;
        return userRepository.findByProviderId(resolvedSteamId)
                .orElseGet(() -> userRepository.save(createSteamUser(resolvedSteamId)));
    }

    @Override
    public String extractSteamId(String claimedId) {
        if (claimedId == null || claimedId.isBlank()) {
            return null;
        }

        Matcher matcher = STEAM_ID_PATTERN.matcher(claimedId.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private boolean verifyAssertion(Map<String, String> parameters) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        parameters.forEach((key, value) -> {
            if (key != null && value != null && !"openid.mode".equals(key)) {
                formBuilder.add(key, value);
            }
        });
        formBuilder.add("openid.mode", "check_authentication");

        Request request = new Request.Builder()
                .url(STEAM_OPENID_ENDPOINT)
                .header("User-Agent", "TournamentAggregator/1.0")
                .post(formBuilder.build())
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.warn("Steam verification failed: unexpected HTTP status {}", response.code());
                return false;
            }

            String body = response.body().string();
            return body.contains("is_valid:true");
        } catch (IOException exception) {
            log.error("Error while verifying Steam callback", exception);
            return false;
        }
    }

    private User createSteamUser(String steamId) {
        String username = "steam_" + steamId;
        String email = username + "@steam.local";

        return User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .authProvider(AuthProvider.STEAM)
                .providerId(steamId)
                .role(UserRole.USER)
                .isEnabled(true)
                .build();
    }
}


