package com.example.tournament_aggregator.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "player_statistics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "player_name")
    private String playerName;

    @Column(name = "hero_name")
    private String heroName;

    @Column(name = "kills")
    @Builder.Default
    private Integer kills = 0;

    @Column(name = "deaths")
    @Builder.Default
    private Integer deaths = 0;

    @Column(name = "assists")
    @Builder.Default
    private Integer assists = 0;

    @Column(name = "last_hits")
    @Builder.Default
    private Integer lastHits = 0;

    @Column(name = "denies")
    @Builder.Default
    private Integer denies = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
