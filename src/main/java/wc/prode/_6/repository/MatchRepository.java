package wc.prode._6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wc.prode._6.entity.Match;
import wc.prode._6.entity.Phase;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByPhase(Phase phase);
    List<Match> findByPhaseOrderByDateAsc(Phase phase);
}

