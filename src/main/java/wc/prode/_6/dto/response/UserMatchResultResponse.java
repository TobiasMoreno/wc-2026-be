package wc.prode._6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMatchResultResponse {
    private Long id;
    private MatchResponse match;
    private Integer homeScore;
    private Integer awayScore;
}

