package wc.prode._6.mapper;

import org.mapstruct.Mapper;
import wc.prode._6.dto.response.UserMatchResultResponse;
import wc.prode._6.entity.UserMatchResult;

@Mapper(componentModel = "spring", uses = MatchMapper.class)
public interface UserMatchResultMapper {
    UserMatchResultResponse toResponse(UserMatchResult userMatchResult);
}

