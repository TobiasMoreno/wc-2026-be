package wc.prode._6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wc.prode._6.entity.User;
import wc.prode._6.entity.UserMatchResult;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserMatchResultRepository extends JpaRepository<UserMatchResult, Long> {
    List<UserMatchResult> findByUser(User user);
    Optional<UserMatchResult> findByUserAndMatchId(User user, Long matchId);
    boolean existsByUserAndMatchId(User user, Long matchId);
    List<UserMatchResult> findByMatchId(Long matchId);
    List<UserMatchResult> findByUserId(Long userId);
}

