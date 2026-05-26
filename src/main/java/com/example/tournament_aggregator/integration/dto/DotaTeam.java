package com.example.tournament_aggregator.integration.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DotaTeam {

    @SerializedName("team_id")
    private Long teamId;

    private String name;

    @SerializedName("logo_url")
    private String logoUrl;

    private String tag;

    @SerializedName("wins")
    private Integer wins;

    @SerializedName("losses")
    private Integer losses;

    private Double rating;
}

