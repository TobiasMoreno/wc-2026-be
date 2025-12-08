package wc.prode._6.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import wc.prode._6.dto.request.UserPreferencesRequest;
import wc.prode._6.dto.response.ApiResponse;
import wc.prode._6.dto.response.UserPreferencesResponse;
import wc.prode._6.service.UserPreferencesService;

@RestController
@RequestMapping("/user/preferences")
@RequiredArgsConstructor
public class UserPreferencesController {

    private final UserPreferencesService userPreferencesService;

    @GetMapping
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> getUserPreferences(
            Authentication authentication) {
        String userEmail = authentication.getName();
        UserPreferencesResponse preferences = userPreferencesService.getUserPreferences(userEmail);
        ApiResponse<UserPreferencesResponse> response = ApiResponse.<UserPreferencesResponse>builder()
                .success(true)
                .message("User preferences retrieved successfully")
                .data(preferences)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserPreferencesResponse>> updateUserPreferences(
            @Valid @RequestBody UserPreferencesRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        UserPreferencesResponse preferences = userPreferencesService.updateUserPreferences(userEmail, request);
        ApiResponse<UserPreferencesResponse> response = ApiResponse.<UserPreferencesResponse>builder()
                .success(true)
                .message("User preferences updated successfully")
                .data(preferences)
                .build();
        return ResponseEntity.ok(response);
    }
}

