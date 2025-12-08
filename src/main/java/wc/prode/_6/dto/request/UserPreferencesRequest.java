package wc.prode._6.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesRequest {
    private String timezone;
    private String language;
    private Boolean notificationsEnabled;
}

