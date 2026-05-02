package com.example.tournament_aggregator.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamResponse {

    private Long id;
    private String name;
    private String tag;
    private String logoUrl;
    private String description;
    private Double winRate;
    private Integer totalMatches;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

