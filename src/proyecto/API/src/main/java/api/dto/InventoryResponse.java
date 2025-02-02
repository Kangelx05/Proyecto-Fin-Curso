package api.dto;

import api.domain.Inventory;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link Inventory}
 */
public record InventoryResponse(Integer id, @NotNull ProductResponse product, Integer amount) implements Serializable {

}