package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.controller.support.CurrentUserResolver;
import com.example.tournament_aggregator.exception.ResourceNotFoundException;
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
@RequestMapping("/subscriptions")
public class SubscriptionPageController {

    private final UserSubscriptionService userSubscriptionService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping
    public String mySubscriptions(Authentication authentication, Model model) {
        Long userId = currentUserResolver.requireUserId(authentication);
        model.addAttribute("subscriptions", userSubscriptionService.getSubscriptionsForUser(userId));
        model.addAttribute("pageTitle", "Мои подписки");
        return "subscriptions/list";
    }

    @PostMapping("/{subscriptionId}/unsubscribe")
    public String unsubscribe(@PathVariable Long subscriptionId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            Long userId = currentUserResolver.requireUserId(authentication);
            userSubscriptionService.unsubscribeUserFromTeam(userId, subscriptionId);
            redirectAttributes.addFlashAttribute("successMessage", "Подписка удалена.");
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", "Подписка не найдена.");
        }
        return "redirect:/subscriptions";
    }
}
