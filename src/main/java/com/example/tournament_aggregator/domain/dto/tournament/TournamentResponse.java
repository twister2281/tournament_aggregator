package com.example.tournament_aggregator.domain.dto.tournament;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TournamentResponse {

    private Long id;
    private String name;
    private String description;
    private Double prizePool;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private Boolean isActive;
    private Set<Long> teamIds;
    private Set<Long> matchIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

