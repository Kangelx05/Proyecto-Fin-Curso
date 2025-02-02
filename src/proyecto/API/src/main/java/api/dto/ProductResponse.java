package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.Product}
 */
public record ProductResponse(Integer id, @NotNull @Size(max = 45) String name, @NotNull Integer price,
                              @Size(max = 45) String category, String desc) implements Serializable {
}