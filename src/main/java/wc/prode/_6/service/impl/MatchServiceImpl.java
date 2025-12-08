package wc.prode._6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import wc.prode._6.dto.response.MatchResponse;
import wc.prode._6.entity.Match;
import wc.prode._6.entity.Phase;
import wc.prode._6.exception.ResourceNotFoundException;
import wc.prode._6.mapper.MatchMapper;
import wc.prode._6.repository.MatchRepository;
import wc.prode._6.service.MatchService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final MatchMapper matchMapper;

    @Override
    public List<MatchResponse> getAllMatches() {
        List<Match> matches = matchRepository.findAll();
        return matches.stream()
                .map(matchMapper::toResponse)
                .toList();
    }

    @Override
    public MatchResponse getMatchById(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        return matchMapper.toResponse(match);
    }

    @Override
    public List<MatchResponse> getMatchesByPhase(Phase phase) {
        List<Match> matches = matchRepository.findByPhaseOrderByDateAsc(phase);
        return matches.stream()
                .map(matchMapper::toResponse)
                .toList();
    }
}

