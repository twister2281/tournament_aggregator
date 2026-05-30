package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.controller.support.CurrentUserResolver;
import com.example.tournament_aggregator.domain.dto.prediction.PredictionResponse;
import com.example.tournament_aggregator.exception.ResourceNotFoundException;
import com.example.tournament_aggregator.service.TournamentPageService;
import com.example.tournament_aggregator.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tournaments")
public class TournamentPageController {

    private final TournamentPageService tournamentPageService;
    private final PredictionService predictionService;
    private final CurrentUserResolver currentUserResolver;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tournaments", tournamentPageService.getTournamentSummaries());
        model.addAttribute("pageTitle", "Турниры");
        return "tournaments/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication authentication, Model model) {
        var tournament = tournamentPageService.getTournamentDetail(id);
        model.addAttribute("tournament", tournament);
        model.addAttribute("pageTitle", "Турнир");
        model.addAttribute("userPredictionsByMatchId", resolveUserPredictions(authentication, id));
        return "tournaments/detail";
    }

    @PostMapping("/{tournamentId}/matches/{matchId}/prediction")
    public String savePrediction(@PathVariable Long tournamentId,
                                 @PathVariable Long matchId,
                                 @RequestParam Long predictedWinnerId,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        if (currentUserResolver.resolveUserId(authentication).isEmpty()) {
            return "redirect:/login";
        }

        try {
            Long userId = currentUserResolver.requireUserId(authentication);
            predictionService.saveUserPrediction(userId, matchId, predictedWinnerId);
            redirectAttributes.addFlashAttribute("successMessage", "Прогноз сохранён.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        } catch (IllegalStateException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        } catch (ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/tournaments/" + tournamentId + "#match-" + matchId;
    }

    private Map<Long, Long> resolveUserPredictions(Authentication authentication, Long tournamentId) {
        return currentUserResolver.resolveUserId(authentication)
                .map(userId -> predictionService.getPredictionsForUserAndTournament(userId, tournamentId).stream()
                        .collect(Collectors.toMap(
                                PredictionResponse::getMatchId,
                                PredictionResponse::getPredictedWinnerId,
                                (left, right) -> left)))
                .orElse(Map.of());
    }
}
