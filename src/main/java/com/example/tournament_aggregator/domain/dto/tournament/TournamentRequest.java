package com.example.tournament_aggregator.domain.dto.tournament;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentRequest {

    @NotBlank(message = "Tournament name must not be blank")
    private String name;

    private String description;
    private Double prizePool;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Boolean isActive;
    private Set<Long> teamIds;
}

