package wc.prode._6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingEntryResponse {
    private Long userId;
    private String userName;
    private String userEmail;
    private String pictureUrl;
    private Integer totalPoints;
    private Integer position;
}

