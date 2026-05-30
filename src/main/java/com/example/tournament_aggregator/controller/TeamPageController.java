package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.controller.support.CurrentUserResolver;
import com.example.tournament_aggregator.service.UserSubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamPageController {

    private final UserSubscriptionService userSubscriptionService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping
    public String list(Authentication authentication, Model model) {
        Long userId = currentUserResolver.resolveUserId(authentication).orElse(null);
        model.addAttribute("teams", userSubscriptionService.getTeamCardsForUser(userId));
        model.addAttribute("pageTitle", "Команды");
        return "teams/list";
    }

    @PostMapping("/{teamId}/subscribe")
    public String subscribe(@PathVariable Long teamId,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        if (currentUserResolver.resolveUserId(authentication).isEmpty()) {
            return "redirect:/login";
        }

        try {
            Long userId = currentUserResolver.requireUserId(authentication);
            userSubscriptionService.subscribeUserToTeam(userId, teamId);
            redirectAttributes.addFlashAttribute("successMessage", "Вы подписались на команду.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/teams";
    }

    @PostMapping("/{teamId}/unsubscribe")
    public String unsubscribe(@PathVariable Long teamId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        if (currentUserResolver.resolveUserId(authentication).isEmpty()) {
            return "redirect:/login";
        }

        try {
            Long userId = currentUserResolver.requireUserId(authentication);
            userSubscriptionService.unsubscribeUserFromTeamByTeamId(userId, teamId);
            redirectAttributes.addFlashAttribute("successMessage", "Подписка отменена.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        } catch (com.example.tournament_aggregator.exception.ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/teams";
    }
}
