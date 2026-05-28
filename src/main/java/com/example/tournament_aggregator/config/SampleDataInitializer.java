package com.example.tournament_aggregator.config;

import com.example.tournament_aggregator.domain.entity.Match;
import com.example.tournament_aggregator.domain.entity.PlayerStatistic;
import com.example.tournament_aggregator.domain.entity.Prediction;
import com.example.tournament_aggregator.domain.entity.Team;
import com.example.tournament_aggregator.domain.entity.Tournament;
import com.example.tournament_aggregator.domain.entity.User;
import com.example.tournament_aggregator.domain.entity.UserSubscription;
import com.example.tournament_aggregator.domain.enums.AuthProvider;
import com.example.tournament_aggregator.domain.enums.UserRole;
import com.example.tournament_aggregator.domain.repository.MatchRepository;
import com.example.tournament_aggregator.domain.repository.PlayerStatisticRepository;
import com.example.tournament_aggregator.domain.repository.PredictionRepository;
import com.example.tournament_aggregator.domain.repository.TeamRepository;
import com.example.tournament_aggregator.domain.repository.TournamentRepository;
import com.example.tournament_aggregator.domain.repository.UserRepository;
import com.example.tournament_aggregator.domain.repository.UserSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class SampleDataInitializer {

    private static final String MAIN_TOURNAMENT_NAME = "Oris Championship 2026";

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final MatchRepository matchRepository;
    private final PlayerStatisticRepository playerStatisticRepository;
    private final PredictionRepository predictionRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void seedSampleData() {
        if (tournamentRepository.findByName(MAIN_TOURNAMENT_NAME).isPresent()) {
            return;
        }

        List<User> users = createUsers();
        List<Team> teams = createTeams();
        Tournament mainTournament = createMainTournament(teams);
        createExtraTournaments(teams);
        List<Match> matches = createMainMatches(mainTournament, teams);
        createPlayerStatistics(matches);
        createPredictions(users, matches);
        createSubscriptions(users, teams);
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            final int index = i;
            String username = String.format("player%02d", i);
            User user = userRepository.findByUsername(username).orElseGet(() -> userRepository.save(User.builder()
                    .username(username)
                    .email(username + "@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .firstName("Player " + index)
                    .lastName("Test")
                    .authProvider(AuthProvider.LOCAL)
                    .role(index == 1 ? UserRole.ADMIN : UserRole.USER)
                    .isEnabled(true)
                    .build()));
            users.add(user);
        }
        return users;
    }

    private List<Team> createTeams() {
        String[][] seedTeams = {
                {"Aurora United", "AUR"},
                {"Blaze Core", "BLZ"},
                {"Cipher Squad", "CPF"},
                {"Delta Force", "DLT"},
                {"Eclipse Gaming", "ECL"},
                {"Falcon Pulse", "FLC"},
                {"Gravity Nine", "GRV"},
                {"Horizon X", "HRZ"},
                {"Inferno Peak", "IFR"},
                {"Jupiter Prime", "JUP"}
        };

        List<Team> teams = new ArrayList<>();
        for (String[] seedTeam : seedTeams) {
            String name = seedTeam[0];
            String tag = seedTeam[1];
            Team team = teamRepository.findByName(name).orElseGet(() -> teamRepository.save(Team.builder()
                    .name(name)
                    .tag(tag)
                    .logoUrl("https://placehold.co/128x128?text=" + tag)
                    .description("Test team " + tag + " for tournament demo data")
                    .winRate(0.0)
                    .totalMatches(0)
                    .build()));
            teams.add(team);
        }
        return teams;
    }

    private Tournament createMainTournament(List<Team> teams) {
        Tournament tournament = tournamentRepository.findByName(MAIN_TOURNAMENT_NAME).orElseGet(() -> {
            Tournament created = Tournament.builder()
                    .name(MAIN_TOURNAMENT_NAME)
                    .description("Main sample tournament with a full bracket and 10 participating teams.")
                    .prizePool(50000.0)
                    .startDate(LocalDateTime.of(2026, 6, 1, 12, 0))
                    .endDate(LocalDateTime.of(2026, 6, 14, 20, 0))
                    .location("Online / EU West")
                    .isActive(true)
                    .teams(new LinkedHashSet<>(teams))
                    .build();
            return tournamentRepository.save(created);
        });

        tournament.setTeams(new LinkedHashSet<>(teams));
        return tournamentRepository.save(tournament);
    }

    private void createExtraTournaments(List<Team> teams) {
        for (int i = 1; i <= 9; i++) {
            String name = "Qualifier Cup " + i;
            if (tournamentRepository.findByName(name).isPresent()) {
                continue;
            }

            Set<Team> subset = new LinkedHashSet<>();
            subset.add(teams.get((i - 1) % teams.size()));
            subset.add(teams.get(i % teams.size()));
            subset.add(teams.get((i + 1) % teams.size()));
            subset.add(teams.get((i + 2) % teams.size()));

            tournamentRepository.save(Tournament.builder()
                    .name(name)
                    .description("Secondary sample tournament #" + i)
                    .prizePool(5000.0 * i)
                    .startDate(LocalDateTime.of(2026, 5, 1, 10, 0).plusDays(i))
                    .endDate(LocalDateTime.of(2026, 5, 2, 18, 0).plusDays(i))
                    .location("Region " + i)
                    .isActive(i % 2 == 0)
                    .teams(subset)
                    .build());
        }
    }

    private List<Match> createMainMatches(Tournament tournament, List<Team> teams) {
        List<MatchSeed> seeds = List.of(
                new MatchSeed(1, teams.get(0), teams.get(1), 2, 1, teams.get(0).getId()),
                new MatchSeed(1, teams.get(2), teams.get(3), 1, 2, teams.get(3).getId()),
                new MatchSeed(2, teams.get(4), teams.get(5), 2, 0, teams.get(4).getId()),
                new MatchSeed(2, teams.get(6), teams.get(7), 0, 2, teams.get(7).getId()),
                new MatchSeed(2, teams.get(8), teams.get(9), 2, 1, teams.get(8).getId()),
                new MatchSeed(2, teams.get(0), teams.get(3), 2, 1, teams.get(0).getId()),
                new MatchSeed(3, teams.get(4), teams.get(7), 1, 2, teams.get(7).getId()),
                new MatchSeed(3, teams.get(8), teams.get(0), 1, 2, teams.get(0).getId()),
                new MatchSeed(4, teams.get(0), teams.get(7), 3, 2, teams.get(0).getId()),
                new MatchSeed(4, teams.get(3), teams.get(8), 2, 1, teams.get(3).getId())
        );

        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < seeds.size(); i++) {
            MatchSeed seed = seeds.get(i);
            Match match = Match.builder()
                    .tournament(tournament)
                    .team1(seed.team1())
                    .team2(seed.team2())
                    .roundNumber(seed.roundNumber())
                    .team1Score(seed.team1Score())
                    .team2Score(seed.team2Score())
                    .matchDate(LocalDateTime.of(2026, 6, 2, 12, 0).plusHours(i * 3L))
                    .isCompleted(true)
                    .winnerId(seed.winnerId())
                    .build();
            matches.add(matchRepository.save(match));
        }
        return matches;
    }

    private void createPlayerStatistics(List<Match> matches) {
        for (int i = 0; i < 10; i++) {
            Match match = matches.get(i % matches.size());
            Team team = (i % 2 == 0) ? match.getTeam1() : match.getTeam2();
            playerStatisticRepository.save(PlayerStatistic.builder()
                    .match(match)
                    .team(team)
                    .playerName("PlayerStat " + (i + 1))
                    .heroName("Hero " + (i + 1))
                    .kills(10 + i)
                    .deaths(i % 5)
                    .assists(5 + i)
                    .lastHits(100 + i * 3)
                    .denies(10 + i)
                    .build());
        }
    }

    private void createPredictions(List<User> users, List<Match> matches) {
        for (int i = 0; i < 10; i++) {
            Match match = matches.get(i % matches.size());
            long predictedWinnerId = (i % 3 == 0) ? match.getWinnerId() : match.getTeam2().getId();
            predictionRepository.save(Prediction.builder()
                    .user(users.get(i))
                    .match(match)
                    .predictedWinnerId(predictedWinnerId)
                    .isCorrect(match.getWinnerId() != null && match.getWinnerId().equals(predictedWinnerId))
                    .build());
        }
    }

    private void createSubscriptions(List<User> users, List<Team> teams) {
        for (int i = 0; i < 10; i++) {
            userSubscriptionRepository.save(UserSubscription.builder()
                    .user(users.get(i))
                    .team(teams.get(i))
                    .build());
        }
    }

    private record MatchSeed(int roundNumber, Team team1, Team team2, Integer team1Score, Integer team2Score, Long winnerId) {
    }
}






