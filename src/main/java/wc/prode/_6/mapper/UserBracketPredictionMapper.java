package wc.prode._6.mapper;

import org.mapstruct.Mapper;
import wc.prode._6.dto.response.UserBracketPredictionResponse;
import wc.prode._6.entity.UserBracketPrediction;

@Mapper(componentModel = "spring", uses = {MatchMapper.class, TeamMapper.class})
public interface UserBracketPredictionMapper {
    UserBracketPredictionResponse toResponse(UserBracketPrediction userBracketPrediction);
}

