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

    @ModelAttribute("isAdmin")
    public boolean isAdmin(Authentication authentication) {
        return hasAdminRole(authentication);
    }

    private boolean hasAuthenticatedUser(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private boolean hasAdminRole(Authentication authentication) {
        if (!hasAuthenticatedUser(authentication)) {
            return false;
        }

        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}



