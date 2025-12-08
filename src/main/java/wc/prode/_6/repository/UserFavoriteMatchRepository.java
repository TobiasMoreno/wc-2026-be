package wc.prode._6.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wc.prode._6.entity.User;
import wc.prode._6.entity.UserFavoriteMatch;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteMatchRepository extends JpaRepository<UserFavoriteMatch, Long> {
    List<UserFavoriteMatch> findByUser(User user);
    Optional<UserFavoriteMatch> findByUserAndMatchId(User user, Long matchId);
    boolean existsByUserAndMatchId(User user, Long matchId);
}

