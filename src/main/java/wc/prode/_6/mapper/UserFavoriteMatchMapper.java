package wc.prode._6.mapper;

import org.mapstruct.Mapper;
import wc.prode._6.dto.response.UserFavoriteMatchResponse;
import wc.prode._6.entity.UserFavoriteMatch;

@Mapper(componentModel = "spring", uses = MatchMapper.class)
public interface UserFavoriteMatchMapper {
    UserFavoriteMatchResponse toResponse(UserFavoriteMatch userFavoriteMatch);
}

