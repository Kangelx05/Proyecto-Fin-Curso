package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link api.domain.OrderStateHistory}
 */
public record OrderStateHistoryResponse(Integer id, @NotNull OrderResponse order, @Size(max = 45) String oldState,
                                        @NotNull @Size(max = 45) String newState, @NotNull Instant date,
                                        @NotNull UserResponse user) implements Serializable {
}