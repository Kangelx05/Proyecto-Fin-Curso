package api.dto;

import lombok.Value;
import java.io.Serializable;

/**
 * Response body for user login.  Contains the JWT that should be used for
 * authenticated requests.  No additional information is returned to
 * minimise exposure of user details.
 */
@Value
public class LoginResponse implements Serializable {
    String token;
}