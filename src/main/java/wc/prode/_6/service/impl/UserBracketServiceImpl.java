package wc.prode._6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wc.prode._6.dto.request.UserBracketPredictionRequest;
import wc.prode._6.dto.response.UserBracketPredictionResponse;
import wc.prode._6.entity.Match;
import wc.prode._6.entity.Phase;
import wc.prode._6.entity.Team;
import wc.prode._6.entity.User;
import wc.prode._6.entity.UserBracketPrediction;
import wc.prode._6.exception.ResourceNotFoundException;
import wc.prode._6.mapper.UserBracketPredictionMapper;
import wc.prode._6.repository.MatchRepository;
import wc.prode._6.repository.TeamRepository;
import wc.prode._6.repository.UserBracketPredictionRepository;
import wc.prode._6.repository.UserRepository;
import wc.prode._6.service.UserBracketService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBracketServiceImpl implements UserBracketService {

    private final UserBracketPredictionRepository userBracketPredictionRepository;
    private final UserRepository userRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final UserBracketPredictionMapper userBracketPredictionMapper;

    @Override
    public List<UserBracketPredictionResponse> getUserBracketPredictions(String userEmail) {
        User user = getUserByEmail(userEmail);
        List<UserBracketPrediction> predictions = userBracketPredictionRepository.findByUser(user);
        return predictions.stream()
                .map(userBracketPredictionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserBracketPredictionResponse createOrUpdatePrediction(String userEmail, UserBracketPredictionRequest request) {
        User user = getUserByEmail(userEmail);
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + request.getMatchId()));

        UserBracketPrediction prediction = userBracketPredictionRepository.findByUserAndMatchId(user, request.getMatchId())
                .orElse(UserBracketPrediction.builder()
                        .user(user)
                        .match(match)
                        .build());

        if (request.getPredictedWinnerId() != null) {
            Team predictedWinner = teamRepository.findById(request.getPredictedWinnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + request.getPredictedWinnerId()));
            prediction.setPredictedWinner(predictedWinner);
        } else {
            prediction.setPredictedWinner(null);
        }

        prediction = userBracketPredictionRepository.save(prediction);
        return userBracketPredictionMapper.toResponse(prediction);
    }

    @Override
    public List<UserBracketPredictionResponse> getUserBracketPredictionsByPhase(String userEmail, Phase phase) {
        User user = getUserByEmail(userEmail);
        List<UserBracketPrediction> predictions = userBracketPredictionRepository.findByUserAndMatchPhase(user, phase);
        return predictions.stream()
                .map(userBracketPredictionMapper::toResponse)
                .toList();
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}

