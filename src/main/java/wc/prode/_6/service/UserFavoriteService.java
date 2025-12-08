package wc.prode._6.service;

import wc.prode._6.dto.response.UserFavoriteMatchResponse;

import java.util.List;

public interface UserFavoriteService {
    List<UserFavoriteMatchResponse> getUserFavorites(String userEmail);
    UserFavoriteMatchResponse addFavorite(String userEmail, Long matchId);
    void removeFavorite(String userEmail, Long matchId);
    boolean isFavorite(String userEmail, Long matchId);
}

