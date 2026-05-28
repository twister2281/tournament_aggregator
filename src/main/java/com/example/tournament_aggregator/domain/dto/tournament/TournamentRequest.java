package com.example.tournament_aggregator.domain.dto.tournament;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
    @Size(max = 255, message = "Tournament name must be at most 255 characters")
    private String name;

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;
    private Double prizePool;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;
    private Boolean isActive;
    private Set<Long> teamIds;
}

