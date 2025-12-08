package wc.prode._6.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wc.prode._6.entity.PredictedResult;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMatchResultRequest {
    @NotNull(message = "Match ID is required")
    private Long matchId;

    @NotNull(message = "Predicted result is required")
    private PredictedResult predictedResult;
}

