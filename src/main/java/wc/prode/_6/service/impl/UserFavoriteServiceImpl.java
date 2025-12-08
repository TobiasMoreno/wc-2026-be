package wc.prode._6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wc.prode._6.dto.response.UserFavoriteMatchResponse;
import wc.prode._6.entity.Match;
import wc.prode._6.entity.User;
import wc.prode._6.entity.UserFavoriteMatch;
import wc.prode._6.exception.BadRequestException;
import wc.prode._6.exception.ResourceNotFoundException;
import wc.prode._6.mapper.UserFavoriteMatchMapper;
import wc.prode._6.repository.MatchRepository;
import wc.prode._6.repository.UserFavoriteMatchRepository;
import wc.prode._6.repository.UserRepository;
import wc.prode._6.service.UserFavoriteService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFavoriteServiceImpl implements UserFavoriteService {

    private final UserFavoriteMatchRepository userFavoriteMatchRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final UserFavoriteMatchMapper userFavoriteMatchMapper;

    @Override
    public List<UserFavoriteMatchResponse> getUserFavorites(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<UserFavoriteMatch> favorites = userFavoriteMatchRepository.findByUser(user);
        return favorites.stream()
                .map(userFavoriteMatchMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserFavoriteMatchResponse addFavorite(String userEmail, Long matchId) {
        User user = getUserByEmail(userEmail);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + matchId));

        if (userFavoriteMatchRepository.existsByUserAndMatchId(user, matchId)) {
            throw new BadRequestException("Match is already in favorites");
        }

        UserFavoriteMatch favorite = UserFavoriteMatch.builder()
                .user(user)
                .match(match)
                .build();

        favorite = userFavoriteMatchRepository.save(favorite);
        return userFavoriteMatchMapper.toResponse(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(String userEmail, Long matchId) {
        User user = getUserByEmail(userEmail);
        UserFavoriteMatch favorite = userFavoriteMatchRepository.findByUserAndMatchId(user, matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite match not found"));
        userFavoriteMatchRepository.delete(favorite);
    }

    @Override
    public boolean isFavorite(String userEmail, Long matchId) {
        User user = getUserByEmail(userEmail);
        return userFavoriteMatchRepository.existsByUserAndMatchId(user, matchId);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}

