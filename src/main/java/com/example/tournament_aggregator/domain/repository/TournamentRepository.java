package com.example.tournament_aggregator.domain.repository;

import com.example.tournament_aggregator.domain.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

	Optional<Tournament> findByName(String name);

	@Query("""
		select distinct t
		from Tournament t
		order by t.startDate desc, t.id desc
		""")
	List<Tournament> findAllOrderedForPublicView();

	@Query("""
		select distinct t
		from Tournament t
		where (select count(m) from Match m where m.tournament = t) >= :minMatches
		order by t.startDate desc, t.id desc
		""")
	List<Tournament> findTournamentsWithAtLeastMatches(@Param("minMatches") long minMatches);
}
