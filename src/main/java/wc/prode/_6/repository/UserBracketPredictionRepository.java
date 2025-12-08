package wc.prode._6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import wc.prode._6.entity.Phase;
import wc.prode._6.entity.User;
import wc.prode._6.entity.UserBracketPrediction;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBracketPredictionRepository extends JpaRepository<UserBracketPrediction, Long> {
    List<UserBracketPrediction> findByUser(User user);
    Optional<UserBracketPrediction> findByUserAndMatchId(User user, Long matchId);
    List<UserBracketPrediction> findByUserAndMatchPhase(User user, Phase phase);
}

