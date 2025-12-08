package wc.prode._6.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMatchResultRequest {
    @NotNull(message = "Home score is required")
    private Integer homeScore;

    @NotNull(message = "Away score is required")
    private Integer awayScore;
}

