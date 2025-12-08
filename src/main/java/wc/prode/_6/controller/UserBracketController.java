package wc.prode._6.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import wc.prode._6.dto.request.UserBracketPredictionRequest;
import wc.prode._6.dto.response.ApiResponse;
import wc.prode._6.dto.response.UserBracketPredictionResponse;
import wc.prode._6.entity.Phase;
import wc.prode._6.service.UserBracketService;

import java.util.List;

@RestController
@RequestMapping("/user/bracket")
@RequiredArgsConstructor
public class UserBracketController {

    private final UserBracketService userBracketService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserBracketPredictionResponse>>> getUserBracketPredictions(
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<UserBracketPredictionResponse> predictions = userBracketService.getUserBracketPredictions(userEmail);
        ApiResponse<List<UserBracketPredictionResponse>> response = ApiResponse.<List<UserBracketPredictionResponse>>builder()
                .success(true)
                .message("User bracket predictions retrieved successfully")
                .data(predictions)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserBracketPredictionResponse>> createOrUpdatePrediction(
            @Valid @RequestBody UserBracketPredictionRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        UserBracketPredictionResponse prediction = userBracketService.createOrUpdatePrediction(userEmail, request);
        ApiResponse<UserBracketPredictionResponse> response = ApiResponse.<UserBracketPredictionResponse>builder()
                .success(true)
                .message("Bracket prediction saved successfully")
                .data(prediction)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/phase/{phase}")
    public ResponseEntity<ApiResponse<List<UserBracketPredictionResponse>>> getUserBracketPredictionsByPhase(
            @PathVariable String phase,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Phase phaseEnum = Phase.valueOf(phase.toUpperCase());
        List<UserBracketPredictionResponse> predictions = userBracketService.getUserBracketPredictionsByPhase(userEmail, phaseEnum);
        ApiResponse<List<UserBracketPredictionResponse>> response = ApiResponse.<List<UserBracketPredictionResponse>>builder()
                .success(true)
                .message("User bracket predictions retrieved successfully")
                .data(predictions)
                .build();
        return ResponseEntity.ok(response);
    }
}

