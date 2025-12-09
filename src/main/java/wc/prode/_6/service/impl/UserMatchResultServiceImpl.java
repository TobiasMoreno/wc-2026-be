package wc.prode._6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wc.prode._6.dto.request.UserMatchResultRequest;
import wc.prode._6.dto.response.UserMatchResultResponse;
import wc.prode._6.entity.Match;
import wc.prode._6.entity.User;
import wc.prode._6.entity.UserMatchResult;
import wc.prode._6.exception.ResourceNotFoundException;
import wc.prode._6.mapper.UserMatchResultMapper;
import wc.prode._6.repository.MatchRepository;
import wc.prode._6.repository.UserMatchResultRepository;
import wc.prode._6.repository.UserRepository;
import wc.prode._6.service.UserMatchResultService;
import wc.prode._6.exception.BadRequestException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserMatchResultServiceImpl implements UserMatchResultService {

    private final UserMatchResultRepository userMatchResultRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final UserMatchResultMapper userMatchResultMapper;

    @Override
    public List<UserMatchResultResponse> getUserMatchResults(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<UserMatchResult> results = userMatchResultRepository.findByUser(user);
        return results.stream()
                .map(userMatchResultMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserMatchResultResponse createOrUpdateUserMatchResult(String userEmail, UserMatchResultRequest request) {
        User user = getUserByEmail(userEmail);
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + request.getMatchId()));

        // Validar que no se pueda apostar menos de 1 hora antes del partido
        validateBettingDeadline(match);
        
        // Validar que el partido no tenga resultado aún
        validateMatchHasNoResult(match);

        UserMatchResult result = userMatchResultRepository.findByUserAndMatchId(user, request.getMatchId())
                .orElse(UserMatchResult.builder()
                        .user(user)
                        .match(match)
                        .build());

        result.setPredictedResult(request.getPredictedResult());

        result = userMatchResultRepository.save(result);
        return userMatchResultMapper.toResponse(result);
    }

    private void validateBettingDeadline(Match match) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime matchDate = match.getDate();
        long hoursUntilMatch = ChronoUnit.HOURS.between(now, matchDate);
        
        if (hoursUntilMatch < 1) {
            throw new BadRequestException("Las apuestas se cierran 1 hora antes del partido. Ya no es posible realizar apuestas para este partido.");
        }
    }

    /**
     * Valida que el partido no tenga resultado aún.
     * Si el partido ya tiene resultado, no se pueden crear ni modificar apuestas.
     */
    private void validateMatchHasNoResult(Match match) {
        if (match.getHomeScore() != null && match.getAwayScore() != null) {
            throw new BadRequestException("No se puede modificar la apuesta porque el partido ya tiene un resultado final.");
        }
    }

    @Override
    @Transactional
    public void deleteUserMatchResult(String userEmail, Long matchId) {
        User user = getUserByEmail(userEmail);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + matchId));
        
        // Validar que el partido no tenga resultado aún
        validateMatchHasNoResult(match);
        
        UserMatchResult result = userMatchResultRepository.findByUserAndMatchId(user, matchId)
                .orElseThrow(() -> new ResourceNotFoundException("User match result not found"));
        userMatchResultRepository.delete(result);
    }

    @Override
    public Optional<UserMatchResultResponse> getUserMatchResultByMatchId(String userEmail, Long matchId) {
        User user = getUserByEmail(userEmail);
        return userMatchResultRepository.findByUserAndMatchId(user, matchId)
                .map(userMatchResultMapper::toResponse);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}

