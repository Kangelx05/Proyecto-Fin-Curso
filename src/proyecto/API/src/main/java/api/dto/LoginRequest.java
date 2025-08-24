package api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import java.io.Serializable;

/**
 * Request body for user login.  Carries the username and password fields
 * required to authenticate.  Both fields are mandatory.
 */
@Value
public class LoginRequest implements Serializable {
    @NotBlank
    String username;
    @NotBlank
    String password;
}