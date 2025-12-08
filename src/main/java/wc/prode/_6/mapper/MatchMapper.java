package wc.prode._6.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import wc.prode._6.dto.response.MatchResponse;
import wc.prode._6.entity.Match;

@Mapper(componentModel = "spring", uses = TeamMapper.class)
public interface MatchMapper {
    @Mapping(target = "phase", expression = "java(match.getPhase().name())")
    @Mapping(target = "group", expression = "java(match.getGroup())")
    MatchResponse toResponse(Match match);
}

