package api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.Product}
 */
public record ProductRequest(@NotNull @Size(max = 45) @NotBlank String name, @NotNull @PositiveOrZero Integer price,
                             @Size(max = 45) String category, String desc) implements Serializable {
}