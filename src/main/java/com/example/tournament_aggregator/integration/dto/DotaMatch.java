package com.example.tournament_aggregator.integration.dto;

import com.google.gson.annotations.SerializedName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties({"radiantWin", "radiant"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DotaMatch {

    @SerializedName("match_id")
    private Long matchId;

    @SerializedName(value = "team_name", alternate = {"radiant_name", "team_name_1"})
    private String teamName;

    @SerializedName(value = "opposing_team_name", alternate = {"dire_name", "team_name_2"})
    private String opposingTeamName;

    @SerializedName("duration")
    private Long duration;

    @SerializedName(value = "radiant", alternate = {"is_radiant"})
    private Boolean radiant;

    @SerializedName(value = "radiant_win", alternate = {"winner"})
    private Boolean radiantWin;

    public String getWinner() {
        if (radiantWin == null) {
            return null;
        }
        if (radiant != null) {
            boolean teamWon = radiant.booleanValue() == radiantWin.booleanValue();
            return teamWon ? teamName : opposingTeamName;
        }
        return Boolean.TRUE.equals(radiantWin) ? teamName : opposingTeamName;
    }
}

