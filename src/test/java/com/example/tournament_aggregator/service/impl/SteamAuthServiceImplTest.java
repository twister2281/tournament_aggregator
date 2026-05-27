package com.example.tournament_aggregator.service.impl;

import com.example.tournament_aggregator.domain.repository.UserRepository;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SteamAuthServiceImplTest {

    @Mock
    private OkHttpClient okHttpClient;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SteamAuthServiceImpl steamAuthService;

    @Test
    void extractSteamIdShouldParseSteamProfileUrl() {
        String steamId = steamAuthService.extractSteamId("https://steamcommunity.com/openid/id/76561198000000000");

        assertEquals("76561198000000000", steamId);
    }

    @Test
    void extractSteamIdShouldReturnNullForInvalidValue() {
        assertNull(steamAuthService.extractSteamId("invalid-value"));
    }

    @Test
    void buildAuthenticationUrlShouldContainRequiredOpenIdParameters() {
        String url = steamAuthService.buildAuthenticationUrl(
                "http://localhost:8080/auth/steam/callback",
                "http://localhost:8080/"
        );

        assertTrue(url.contains("steamcommunity.com/openid/login"));
        assertTrue(url.contains("openid.mode=checkid_setup"));
        assertTrue(url.contains("openid.return_to="));
        assertTrue(url.contains("openid.realm="));
        assertTrue(url.contains("openid.identity="));
        assertTrue(url.contains("openid.claimed_id="));
    }
}

