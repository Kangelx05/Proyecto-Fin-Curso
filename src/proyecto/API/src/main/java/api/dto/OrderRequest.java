package api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link api.domain.Order}
 */
public record OrderRequest(@NotNull TableRequest table, @NotNull Instant date,
                           @NotNull @Size(max = 45) @NotBlank String state) implements Serializable {

}