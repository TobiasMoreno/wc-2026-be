package wc.prode._6.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import wc.prode._6.dto.request.UserMatchResultRequest;
import wc.prode._6.dto.response.ApiResponse;
import wc.prode._6.dto.response.UserMatchResultResponse;
import wc.prode._6.service.UserMatchResultService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user/matches/results")
@RequiredArgsConstructor
public class UserMatchController {

    private final UserMatchResultService userMatchResultService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserMatchResultResponse>>> getUserMatchResults(
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<UserMatchResultResponse> results = userMatchResultService.getUserMatchResults(userEmail);
        ApiResponse<List<UserMatchResultResponse>> response = ApiResponse.<List<UserMatchResultResponse>>builder()
                .success(true)
                .message("User match results retrieved successfully")
                .data(results)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserMatchResultResponse>> createOrUpdateUserMatchResult(
            @Valid @RequestBody UserMatchResultRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        UserMatchResultResponse result = userMatchResultService.createOrUpdateUserMatchResult(userEmail, request);
        ApiResponse<UserMatchResultResponse> response = ApiResponse.<UserMatchResultResponse>builder()
                .success(true)
                .message("User match result saved successfully")
                .data(result)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{matchId}")
    public ResponseEntity<ApiResponse<UserMatchResultResponse>> getUserMatchResultByMatchId(
            @PathVariable Long matchId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Optional<UserMatchResultResponse> resultOpt = userMatchResultService.getUserMatchResultByMatchId(userEmail, matchId);
        
        if (resultOpt.isEmpty()) {
            ApiResponse<UserMatchResultResponse> response = ApiResponse.<UserMatchResultResponse>builder()
                    .success(false)
                    .message("User match result not found")
                    .data(null)
                    .build();
            return ResponseEntity.status(404).body(response);
        }
        
        ApiResponse<UserMatchResultResponse> response = ApiResponse.<UserMatchResultResponse>builder()
                .success(true)
                .message("User match result retrieved successfully")
                .data(resultOpt.get())
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<ApiResponse<Object>> deleteUserMatchResult(
            @PathVariable Long matchId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        userMatchResultService.deleteUserMatchResult(userEmail, matchId);
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("User match result deleted successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}

