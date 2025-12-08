package wc.prode._6.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wc.prode._6.dto.request.UserPreferencesRequest;
import wc.prode._6.dto.response.UserPreferencesResponse;
import wc.prode._6.entity.User;
import wc.prode._6.entity.UserPreferences;
import wc.prode._6.exception.ResourceNotFoundException;
import wc.prode._6.mapper.UserPreferencesMapper;
import wc.prode._6.repository.UserPreferencesRepository;
import wc.prode._6.repository.UserRepository;
import wc.prode._6.service.UserPreferencesService;

@Service
@RequiredArgsConstructor
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private final UserPreferencesRepository userPreferencesRepository;
    private final UserRepository userRepository;
    private final UserPreferencesMapper userPreferencesMapper;

    @Override
    public UserPreferencesResponse getUserPreferences(String userEmail) {
        User user = getUserByEmail(userEmail);
        UserPreferences preferences = userPreferencesRepository.findByUser(user)
                .orElseGet(() -> {
                    UserPreferences newPreferences = UserPreferences.builder()
                            .user(user)
                            .language("es")
                            .notificationsEnabled(true)
                            .build();
                    return userPreferencesRepository.save(newPreferences);
                });
        return userPreferencesMapper.toResponse(preferences);
    }

    @Override
    @Transactional
    public UserPreferencesResponse updateUserPreferences(String userEmail, UserPreferencesRequest request) {
        User user = getUserByEmail(userEmail);
        UserPreferences preferences = userPreferencesRepository.findByUser(user)
                .orElseGet(() -> UserPreferences.builder()
                        .user(user)
                        .language("es")
                        .notificationsEnabled(true)
                        .build());

        if (request.getTimezone() != null) {
            preferences.setTimezone(request.getTimezone());
        }
        if (request.getLanguage() != null) {
            preferences.setLanguage(request.getLanguage());
        }
        if (request.getNotificationsEnabled() != null) {
            preferences.setNotificationsEnabled(request.getNotificationsEnabled());
        }

        preferences = userPreferencesRepository.save(preferences);
        return userPreferencesMapper.toResponse(preferences);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}

