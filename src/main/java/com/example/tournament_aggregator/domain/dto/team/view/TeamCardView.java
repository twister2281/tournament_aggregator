package com.example.tournament_aggregator.domain.dto.team.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCardView {

    private Long id;
    private String name;
    private String tag;
    private String logoUrl;
    private String description;
    private Double winRate;
    private Integer totalMatches;
    private boolean subscribed;
    private Long subscriptionId;
}
