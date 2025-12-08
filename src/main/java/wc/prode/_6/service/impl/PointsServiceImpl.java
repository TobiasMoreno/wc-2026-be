package wc.prode._6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wc.prode._6.entity.Match;
import wc.prode._6.entity.PredictedResult;
import wc.prode._6.entity.User;
import wc.prode._6.entity.UserMatchResult;
import wc.prode._6.exception.ResourceNotFoundException;
import wc.prode._6.repository.MatchRepository;
import wc.prode._6.repository.UserMatchResultRepository;
import wc.prode._6.repository.UserRepository;
import wc.prode._6.service.PointsService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointsServiceImpl implements PointsService {

    private final MatchRepository matchRepository;
    private final UserMatchResultRepository userMatchResultRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void calculatePointsForMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + matchId));

        // Solo calcular si el partido tiene resultado final
        if (match.getHomeScore() == null || match.getAwayScore() == null) {
            return;
        }

        // Obtener todas las apuestas para este partido
        List<UserMatchResult> bets = userMatchResultRepository.findByMatchId(matchId);

        // Obtener todos los usuarios únicos que apostaron
        Set<User> users = bets.stream()
                .map(UserMatchResult::getUser)
                .collect(Collectors.toSet());

        // Recalcular puntos de todos los usuarios que apostaron
        for (User user : users) {
            int totalPoints = calculateUserTotalPoints(user.getId());
            user.setTotalPoints(totalPoints);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void recalculateAllPoints() {
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            int totalPoints = calculateUserTotalPoints(user.getId());
            user.setTotalPoints(totalPoints);
            userRepository.save(user);
        }
    }

    /**
     * Calcula los puntos totales de un usuario basándose en todas sus apuestas
     */
    private int calculateUserTotalPoints(Long userId) {
        List<UserMatchResult> userBets = userMatchResultRepository.findByUserId(userId);
        int totalPoints = 0;

        for (UserMatchResult bet : userBets) {
            Match match = bet.getMatch();
            
            // Solo contar puntos si el partido tiene resultado final
            if (match.getHomeScore() != null && match.getAwayScore() != null) {
                int points = calculatePointsForBet(bet, match);
                totalPoints += points;
            }
        }

        return totalPoints;
    }

    /**
     * Calcula los puntos de una apuesta individual
     * - Resultado correcto: 1 punto
     * - Resultado incorrecto: 0 puntos
     */
    private int calculatePointsForBet(UserMatchResult bet, Match match) {
        PredictedResult predictedResult = bet.getPredictedResult();
        Integer actualHomeScore = match.getHomeScore();
        Integer actualAwayScore = match.getAwayScore();

        // Determinar resultado real del partido
        PredictedResult actualResult;
        if (actualHomeScore > actualAwayScore) {
            actualResult = PredictedResult.HOME_WIN;
        } else if (actualHomeScore < actualAwayScore) {
            actualResult = PredictedResult.AWAY_WIN;
        } else {
            actualResult = PredictedResult.DRAW;
        }

        // Si acertó el resultado, da 1 punto
        if (predictedResult == actualResult) {
            return 1;
        }

        // Resultado incorrecto
        return 0;
    }
}

