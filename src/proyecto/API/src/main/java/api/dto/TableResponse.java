package api.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.Table}
 */
public record TableResponse(Integer id, @NotNull @PositiveOrZero Integer num_Table,
                            @NotNull @Positive Integer num_Customers, @NotNull UserResponse waiter,
                            @NotNull @Size(max = 45) @NotBlank String state) implements Serializable {
}