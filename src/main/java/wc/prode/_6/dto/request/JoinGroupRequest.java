package wc.prode._6.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JoinGroupRequest {
    @NotBlank(message = "Group name is required")
    private String groupName;

    @NotBlank(message = "Password is required")
    private String password;
}

