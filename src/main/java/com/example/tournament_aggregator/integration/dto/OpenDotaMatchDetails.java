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
public class OpenDotaMatchDetails {

    @SerializedName("match_id")
    private Long matchId;

    @SerializedName(value = "radiant_name", alternate = {"team_name_1"})
    private String radiantName;

    @SerializedName(value = "dire_name", alternate = {"team_name_2"})
    private String direName;

    @SerializedName("duration")
    private Long duration;

    @SerializedName("radiant_win")
    private Boolean radiantWin;
}

