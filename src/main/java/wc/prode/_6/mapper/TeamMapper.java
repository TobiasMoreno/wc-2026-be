package wc.prode._6.mapper;

import org.mapstruct.Mapper;
import wc.prode._6.dto.response.TeamResponse;
import wc.prode._6.entity.Team;

@Mapper(componentModel = "spring")
public interface TeamMapper {
    TeamResponse toResponse(Team team);
}

