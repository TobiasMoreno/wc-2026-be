package wc.prode._6.mapper;

import org.mapstruct.Mapper;
import wc.prode._6.dto.response.AuthResponse;
import wc.prode._6.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @org.mapstruct.Mapping(target = "token", ignore = true)
    AuthResponse toAuthResponse(User user);
}

