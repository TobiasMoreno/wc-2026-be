package wc.prode._6.service;

import wc.prode._6.dto.request.UserBracketPredictionRequest;
import wc.prode._6.dto.response.UserBracketPredictionResponse;
import wc.prode._6.entity.Phase;

import java.util.List;

public interface UserBracketService {
    List<UserBracketPredictionResponse> getUserBracketPredictions(String userEmail);
    UserBracketPredictionResponse createOrUpdatePrediction(String userEmail, UserBracketPredictionRequest request);
    List<UserBracketPredictionResponse> getUserBracketPredictionsByPhase(String userEmail, Phase phase);
}

