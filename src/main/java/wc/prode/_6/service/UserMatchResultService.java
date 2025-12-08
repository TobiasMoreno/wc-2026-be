package wc.prode._6.service;

import wc.prode._6.dto.request.UserMatchResultRequest;
import wc.prode._6.dto.response.UserMatchResultResponse;

import java.util.List;

public interface UserMatchResultService {
    List<UserMatchResultResponse> getUserMatchResults(String userEmail);
    UserMatchResultResponse createOrUpdateUserMatchResult(String userEmail, UserMatchResultRequest request);
    void deleteUserMatchResult(String userEmail, Long matchId);
    UserMatchResultResponse getUserMatchResultByMatchId(String userEmail, Long matchId);
}

