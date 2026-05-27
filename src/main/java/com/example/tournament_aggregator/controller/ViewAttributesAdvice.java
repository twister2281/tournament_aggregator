package com.example.tournament_aggregator.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ViewAttributesAdvice {

    @ModelAttribute("isAuthenticated")
    public boolean isAuthenticated(Authentication authentication) {
        return hasAuthenticatedUser(authentication);
    }

    @ModelAttribute("currentUsername")
    public String currentUsername(Authentication authentication) {
        if (hasAuthenticatedUser(authentication)) {
            return authentication.getName();
        }
        return null;
    }

    @ModelAttribute("currentRole")
    public String currentRole(Authentication authentication) {
        if (!hasAuthenticatedUser(authentication)) {
            return null;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority() != null && authority.getAuthority().startsWith("ROLE_")) {
                return authority.getAuthority().substring("ROLE_".length());
            }
        }
        return null;
    }

    private boolean hasAuthenticatedUser(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}



