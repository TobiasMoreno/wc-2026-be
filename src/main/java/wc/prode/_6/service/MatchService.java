package wc.prode._6.service;

import wc.prode._6.dto.response.MatchResponse;
import wc.prode._6.entity.Phase;

import java.util.List;

public interface MatchService {
    List<MatchResponse> getAllMatches();
    MatchResponse getMatchById(Long id);
    List<MatchResponse> getMatchesByPhase(Phase phase);
}

