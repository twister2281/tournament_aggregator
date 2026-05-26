package com.example.tournament_aggregator.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamUpdateRequest {

    @NotBlank(message = "Team name must not be blank")
    private String name;

    @NotBlank(message = "Team tag must not be blank")
    @Size(max = 10, message = "Team tag must be at most 10 characters")
    private String tag;

    private String logoUrl;

    private String description;

    @PositiveOrZero(message = "Win rate must be zero or positive")
    private Double winRate;

    @PositiveOrZero(message = "Total matches must be zero or positive")
    private Integer totalMatches;
}
