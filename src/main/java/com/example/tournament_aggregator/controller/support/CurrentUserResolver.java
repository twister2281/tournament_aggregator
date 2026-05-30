package com.example.tournament_aggregator.controller.support;

import com.example.tournament_aggregator.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CurrentUserResolver {

    private final UserRepository userRepository;

    public Optional<Long> resolveUserId(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return Optional.empty();
        }
        return userRepository.findByUsername(authentication.getName()).map(user -> user.getId());
    }

    public Long requireUserId(Authentication authentication) {
        return resolveUserId(authentication)
                .orElseThrow(() -> new IllegalStateException("Требуется вход в систему"));
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}
