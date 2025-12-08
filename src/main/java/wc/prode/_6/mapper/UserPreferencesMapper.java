package wc.prode._6.mapper;

import org.mapstruct.Mapper;
import wc.prode._6.dto.response.UserPreferencesResponse;
import wc.prode._6.entity.UserPreferences;

@Mapper(componentModel = "spring")
public interface UserPreferencesMapper {
    UserPreferencesResponse toResponse(UserPreferences userPreferences);
}

