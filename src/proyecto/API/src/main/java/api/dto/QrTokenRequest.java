package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.QrToken}
 */
@Value
public class QrTokenRequest implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 255)
    String token;
    @NotNull
    int table;
    /**
     * Optional fingerprint of the first device using this token.  May be
     * null if the token has not yet been consumed.
     */
    @Size(max = 255)
    String usedBy;
}