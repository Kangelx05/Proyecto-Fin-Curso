package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.Inventory}
 */
public record InventoryRequest(@NotNull ProductRequest product, @Positive Integer amount) implements Serializable {
}