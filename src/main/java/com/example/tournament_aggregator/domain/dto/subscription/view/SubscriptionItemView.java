package com.example.tournament_aggregator.domain.dto.subscription.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionItemView {

    private Long subscriptionId;
    private Long teamId;
    private String teamName;
    private String teamTag;
    private String logoUrl;
    private Double winRate;
    private Integer totalMatches;
    private LocalDateTime subscribedAt;
}
