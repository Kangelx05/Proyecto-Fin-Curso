package api.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.Table}
 */
public record TableRequest(@NotNull @PositiveOrZero Integer num_Table, @NotNull @Positive Integer num_Customers,
                           @NotNull int waiter,
                           @NotNull @Size(max = 45) @NotBlank String state, Integer posX, Integer posY) implements Serializable {
}