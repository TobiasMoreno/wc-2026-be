package wc.prode._6.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_match_results", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "match_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMatchResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Enumerated(EnumType.STRING)
    @Column(name = "predicted_result", nullable = false)
    private PredictedResult predictedResult;
}

