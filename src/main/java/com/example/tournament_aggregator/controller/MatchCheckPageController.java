package com.example.tournament_aggregator.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchCheckPageController {

    @GetMapping("/")
    public String root() {
        return "redirect:/match-check";
    }

    @GetMapping("/match-check")
    public String matchCheckPage() {
        return "match-check";
    }
}

