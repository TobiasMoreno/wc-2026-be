package wc.prode._6.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchResponse {
    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    private String city;
    private String stadium;
    private String phase;
    private String group; // A-L para fase de grupos, null para eliminatorias
    private TeamResponse homeTeam;
    private TeamResponse awayTeam;
    private Integer homeScore;
    private Integer awayScore;
}

