package wc.prode._6.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import wc.prode._6.dto.response.ApiResponse;
import wc.prode._6.dto.response.UserFavoriteMatchResponse;
import wc.prode._6.service.UserFavoriteService;

import java.util.List;

@RestController
@RequestMapping("/user/matches/favorites")
@RequiredArgsConstructor
public class UserFavoriteController {

    private final UserFavoriteService userFavoriteService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserFavoriteMatchResponse>>> getUserFavorites(
            Authentication authentication) {
        String userEmail = authentication.getName();
        List<UserFavoriteMatchResponse> favorites = userFavoriteService.getUserFavorites(userEmail);
        ApiResponse<List<UserFavoriteMatchResponse>> response = ApiResponse.<List<UserFavoriteMatchResponse>>builder()
                .success(true)
                .message("User favorites retrieved successfully")
                .data(favorites)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{matchId}")
    public ResponseEntity<ApiResponse<UserFavoriteMatchResponse>> addFavorite(
            @PathVariable Long matchId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        UserFavoriteMatchResponse favorite = userFavoriteService.addFavorite(userEmail, matchId);
        ApiResponse<UserFavoriteMatchResponse> response = ApiResponse.<UserFavoriteMatchResponse>builder()
                .success(true)
                .message("Match added to favorites successfully")
                .data(favorite)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<ApiResponse<Object>> removeFavorite(
            @PathVariable Long matchId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        userFavoriteService.removeFavorite(userEmail, matchId);
        ApiResponse<Object> response = ApiResponse.builder()
                .success(true)
                .message("Match removed from favorites successfully")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{matchId}/check")
    public ResponseEntity<ApiResponse<Boolean>> isFavorite(
            @PathVariable Long matchId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        boolean isFavorite = userFavoriteService.isFavorite(userEmail, matchId);
        ApiResponse<Boolean> response = ApiResponse.<Boolean>builder()
                .success(true)
                .message("Favorite status retrieved successfully")
                .data(isFavorite)
                .build();
        return ResponseEntity.ok(response);
    }
}

