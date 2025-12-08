package wc.prode._6.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBracketPredictionRequest {
    @NotNull(message = "Match ID is required")
    private Long matchId;

    private Long predictedWinnerId;
}

