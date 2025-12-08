package wc.prode._6.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wc.prode._6.dto.response.ApiResponse;
import wc.prode._6.dto.response.MatchResponse;
import wc.prode._6.entity.Phase;
import wc.prode._6.service.MatchService;

import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getAllMatches() {
        List<MatchResponse> matches = matchService.getAllMatches();
        ApiResponse<List<MatchResponse>> response = ApiResponse.<List<MatchResponse>>builder()
                .success(true)
                .message("Matches retrieved successfully")
                .data(matches)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MatchResponse>> getMatchById(@PathVariable Long id) {
        MatchResponse match = matchService.getMatchById(id);
        ApiResponse<MatchResponse> response = ApiResponse.<MatchResponse>builder()
                .success(true)
                .message("Match retrieved successfully")
                .data(match)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/phase/{phase}")
    public ResponseEntity<ApiResponse<List<MatchResponse>>> getMatchesByPhase(@PathVariable String phase) {
        Phase phaseEnum = Phase.valueOf(phase.toUpperCase());
        List<MatchResponse> matches = matchService.getMatchesByPhase(phaseEnum);
        ApiResponse<List<MatchResponse>> response = ApiResponse.<List<MatchResponse>>builder()
                .success(true)
                .message("Matches retrieved successfully")
                .data(matches)
                .build();
        return ResponseEntity.ok(response);
    }
}

