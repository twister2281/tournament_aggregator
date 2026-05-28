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
public class TeamCreateRequest {

    @NotBlank(message = "Team name must not be blank")
    @Size(max = 255, message = "Team name must be at most 255 characters")
    private String name;

    @NotBlank(message = "Team tag must not be blank")
    @Size(max = 10, message = "Team tag must be at most 10 characters")
    private String tag;

    @Size(max = 255, message = "Logo URL must be at most 255 characters")
    private String logoUrl;

    @Size(max = 5000, message = "Description must be at most 5000 characters")
    private String description;

    @PositiveOrZero(message = "Win rate must be zero or positive")
    private Double winRate;

    @PositiveOrZero(message = "Total matches must be zero or positive")
    private Integer totalMatches;
}
