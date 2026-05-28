package com.example.tournament_aggregator.controller;

import com.example.tournament_aggregator.service.TournamentPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tournaments")
public class TournamentPageController {

    private final TournamentPageService tournamentPageService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("tournaments", tournamentPageService.getTournamentSummaries());
        model.addAttribute("pageTitle", "Турниры");
        return "tournaments/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("tournament", tournamentPageService.getTournamentDetail(id));
        model.addAttribute("pageTitle", "Турнир");
        return "tournaments/detail";
    }
}

