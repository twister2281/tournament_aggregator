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
public class DotaPro {

    @SerializedName("account_id")
    private Long accountId;

    @SerializedName("steamid")
    private String steamId;

    private String name;

    @SerializedName("team_id")
    private Long teamId;

    @SerializedName("team_name")
    private String teamName;
}

