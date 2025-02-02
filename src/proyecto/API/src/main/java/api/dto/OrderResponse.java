package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link api.domain.Order}
 */
public record OrderResponse(Integer id, @NotNull TableResponse table, @NotNull Instant date,
                            @NotNull @Size(max = 45) String state) implements Serializable {

}