package wc.prode._6.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreferences {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "timezone", length = 50)
    private String timezone;

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "es";

    @Column(name = "notifications_enabled")
    @Builder.Default
    private Boolean notificationsEnabled = true;
}

