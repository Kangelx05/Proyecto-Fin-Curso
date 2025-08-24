package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link api.domain.QrToken}
 */
@Value
public class QrTokenResponse implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 255)
    String token;
    @NotNull
    TableResponse table;
    /** Fingerprint of the first device to use this token. May be null if the token
     *  has not yet been redeemed. */
    String usedBy;
    /** Current status of the token (UNUSED, IN_USE, EXPIRED). */
    @NotNull
    String status;
    /** Expiration timestamp for the token. */
    @NotNull
    LocalDateTime expiresAt;
    /** Creation timestamp for the token. */
    LocalDateTime createdAt;
}