package wc.prode._6.service;

import wc.prode._6.dto.request.UserPreferencesRequest;
import wc.prode._6.dto.response.UserPreferencesResponse;

public interface UserPreferencesService {
    UserPreferencesResponse getUserPreferences(String userEmail);
    UserPreferencesResponse updateUserPreferences(String userEmail, UserPreferencesRequest request);
}

