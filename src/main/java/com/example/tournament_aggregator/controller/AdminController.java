package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.domain.dto.TeamCreateRequest;
import com.example.tournament_aggregator.domain.dto.TeamResponse;
import com.example.tournament_aggregator.domain.dto.match.MatchRequest;
import com.example.tournament_aggregator.domain.dto.tournament.TournamentRequest;
import com.example.tournament_aggregator.domain.dto.tournament.view.TournamentDetailView;
import com.example.tournament_aggregator.domain.dto.tournament.view.TournamentSummaryView;
import com.example.tournament_aggregator.service.TeamService;
import com.example.tournament_aggregator.service.MatchService;
import com.example.tournament_aggregator.service.TournamentPageService;
import com.example.tournament_aggregator.service.TournamentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final TeamService teamService;
    private final MatchService matchService;
    private final TournamentService tournamentService;
    private final TournamentPageService tournamentPageService;

    @GetMapping({"", "/"})
    public String adminRoot() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        prepareModel(model);
        model.addAttribute("tournamentsWith2Matches", tournamentPageService.countTournamentsWithAtLeastMatches(2));
        if (!model.containsAttribute("teamRequest")) {
            model.addAttribute("teamRequest", TeamCreateRequest.builder().build());
        }
        return "admin/dashboard";
    }

    @GetMapping("/tournaments")
    public String tournamentManagement(@RequestParam(value = "tournamentId", required = false) Long tournamentId,
                                       Model model) {
        prepareModel(model);
        model.addAttribute("teamRequest", TeamCreateRequest.builder().build());
        model.addAttribute("tournamentRequest", TournamentRequest.builder().build());
        List<TournamentSummaryView> tournaments = tournamentPageService.getTournamentSummaries();
        Long selectedTournamentId = resolveTournamentId(tournamentId, tournaments);
        TournamentDetailView selectedTournament = selectedTournamentId != null
                ? tournamentPageService.getTournamentDetail(selectedTournamentId)
                : null;
        model.addAttribute("selectedTournamentId", selectedTournamentId);
        model.addAttribute("selectedTournament", selectedTournament);
        return "admin/tournaments";
    }

    @PostMapping("/teams")
    public String createTeam(@Valid @ModelAttribute("teamRequest") TeamCreateRequest teamRequest,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareModel(model);
            model.addAttribute("tournamentRequest", TournamentRequest.builder().build());
            return "admin/dashboard";
        }

        try {
            TeamResponse createdTeam = teamService.createTeam(teamRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Команда «" + createdTeam.getName() + "» создана.");
            return "redirect:/admin/dashboard";
        } catch (IllegalArgumentException exception) {
            prepareModel(model);
            model.addAttribute("tournamentRequest", TournamentRequest.builder().build());
            model.addAttribute("teamFormError", exception.getMessage());
            return "admin/dashboard";
        }
    }

    @PostMapping("/tournaments")
    public String createTournament(@Valid @ModelAttribute("tournamentRequest") TournamentRequest tournamentRequest,
                                   BindingResult bindingResult,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return renderTournamentManagementWithErrors(model, tournamentRequest, "Проверь данные турнира.");
        }

        try {
            var createdTournament = tournamentService.createTournament(tournamentRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Турнир «" + createdTournament.getName() + "» создан.");
            return "redirect:/admin/tournaments?tournamentId=" + createdTournament.getId();
        } catch (IllegalArgumentException exception) {
            return renderTournamentManagementWithErrors(model, tournamentRequest, exception.getMessage());
        }
    }

    @PostMapping("/tournaments/{tournamentId}/matches/{matchId}")
    public String updateMatch(@PathVariable Long tournamentId,
                              @PathVariable Long matchId,
                              @Valid @ModelAttribute MatchRequest matchRequest,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        matchRequest.setTournamentId(tournamentId);

        if (bindingResult.hasErrors()) {
            return renderTournamentManagementWithErrors(model, null, "Проверьте данные матча", tournamentId);
        }

        try {
            matchService.updateMatch(matchId, matchRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Матч #" + matchId + " обновлён.");
            return "redirect:/admin/tournaments?tournamentId=" + tournamentId;
        } catch (IllegalArgumentException exception) {
            return renderTournamentManagementWithErrors(model, null, exception.getMessage(), tournamentId);
        }
    }

    @PostMapping("/tournaments/{tournamentId}/matches")
    public String createMatch(@PathVariable Long tournamentId,
                              @Valid @ModelAttribute MatchRequest matchRequest,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        matchRequest.setTournamentId(tournamentId);

        if (bindingResult.hasErrors()) {
            return renderTournamentManagementWithErrors(model, null, "Проверьте данные нового матча", tournamentId);
        }

        try {
            matchService.createMatch(matchRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Матч добавлен в турнир.");
            return "redirect:/admin/tournaments?tournamentId=" + tournamentId;
        } catch (IllegalArgumentException exception) {
            return renderTournamentManagementWithErrors(model, null, exception.getMessage(), tournamentId);
        }
    }

    private void prepareModel(Model model) {
        List<TeamResponse> teams = teamService.getAllTeams();
        List<TournamentSummaryView> tournaments = tournamentPageService.getTournamentSummaries();
        model.addAttribute("teams", teams);
        model.addAttribute("tournaments", tournaments);
        model.addAttribute("tournamentsWith2Matches", tournamentPageService.countTournamentsWithAtLeastMatches(2));
    }

    private Long resolveTournamentId(Long requestedTournamentId, List<TournamentSummaryView> tournaments) {
        if (requestedTournamentId != null) {
            Long matchedId = tournaments.stream()
                    .filter(tournament -> requestedTournamentId.equals(tournament.getId()))
                    .findFirst()
                    .map(TournamentSummaryView::getId)
                    .orElse(null);
            if (matchedId != null) {
                return matchedId;
            }
        }
        return tournaments.isEmpty() ? null : tournaments.get(0).getId();
    }

    private String renderTournamentManagementWithErrors(Model model,
                                                        TournamentRequest tournamentRequest,
                                                        String errorMessage) {
        return renderTournamentManagementWithErrors(model, tournamentRequest, errorMessage, null);
    }

    private String renderTournamentManagementWithErrors(Model model,
                                                        TournamentRequest tournamentRequest,
                                                        String errorMessage,
                                                        Long selectedTournamentId) {
        prepareModel(model);
        model.addAttribute("teamRequest", TeamCreateRequest.builder().build());
        if (tournamentRequest == null) {
            model.addAttribute("tournamentRequest", TournamentRequest.builder().build());
        } else {
            model.addAttribute("tournamentRequest", tournamentRequest);
        }

        List<TournamentSummaryView> tournaments = tournamentPageService.getTournamentSummaries();

        Long resolvedTournamentId = selectedTournamentId != null ? selectedTournamentId : resolveTournamentId(null, tournaments);
        TournamentDetailView selectedTournament = resolvedTournamentId != null
                ? tournamentPageService.getTournamentDetail(resolvedTournamentId)
                : null;

        model.addAttribute("selectedTournamentId", resolvedTournamentId);
        model.addAttribute("selectedTournament", selectedTournament);
        model.addAttribute("tournamentFormError", errorMessage);
        return "admin/tournaments";
    }
}