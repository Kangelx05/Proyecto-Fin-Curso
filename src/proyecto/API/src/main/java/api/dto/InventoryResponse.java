package api.dto;

import api.domain.Inventory;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link Inventory}
 */
public class InventoryResponse implements Serializable {
    private final Integer id;
    @NotNull
    private final ProductResponse product;
    private final Integer amount;

    public InventoryResponse(Integer id, ProductResponse product, Integer amount) {
        this.id = id;
        this.product = product;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public Integer getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryResponse entity = (InventoryResponse) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.product, entity.product) &&
                Objects.equals(this.amount, entity.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, amount);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "product = " + product + ", " +
                "amount = " + amount + ")";
    }
}