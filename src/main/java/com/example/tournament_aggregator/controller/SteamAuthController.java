package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.domain.entity.User;
import com.example.tournament_aggregator.service.SteamAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth/steam")


public class SteamAuthController {

    private final SteamAuthService steamAuthService;

    private final SecurityContextRepository securityContextRepository;

    @GetMapping
    public void redirectToSteam(HttpServletResponse response) throws IOException {
        String returnTo = ServletUriComponentsBuilder.fromCurrentContextPath().path("/auth/steam/callback").toUriString();


        String realm = ServletUriComponentsBuilder.fromCurrentContextPath().path("/").toUriString();

        response.sendRedirect(steamAuthService.buildAuthenticationUrl(returnTo, realm));
    }

    @GetMapping("/callback")
    public String callback(@RequestParam Map<String, String> parameters,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        try {
            User user = steamAuthService.authenticateCallback(parameters);
            authenticate(user, request, response);
            return "redirect:/match-check?steam=success";
        } catch (IllegalArgumentException exception) {
            return "redirect:/login?steamError";
        }
    }

    private void authenticate(User user, HttpServletRequest request, HttpServletResponse response) {
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );

        UserDetails principal = org.springframework.security.core.userdetails.User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .disabled(Boolean.FALSE.equals(user.getIsEnabled()))
                .build();

        Authentication authentication = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                principal,
                principal.getPassword(),
                principal.getAuthorities()
        );

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }
}


