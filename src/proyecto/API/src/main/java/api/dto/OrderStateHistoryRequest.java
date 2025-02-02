package api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link api.domain.OrderStateHistory}
 */
public record OrderStateHistoryRequest(@NotNull @NotBlank OrderRequest order, @Size(max = 45) String oldState,
                                       @NotNull @NotBlank @Size(max = 45) String newState, @NotNull Instant date,
                                       @NotNull UserRequest user) implements Serializable {
}