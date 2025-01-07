package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link api.domain.Inventory}
 */
public class InventoryRequest implements Serializable {
    @NotNull
    private final ProductRequest product;
    @Positive
    private final Integer amount;

    public InventoryRequest(ProductRequest product, Integer amount) {
        this.product = product;
        this.amount = amount;
    }

    public ProductRequest getProduct() {
        return product;
    }

    public Integer getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryRequest entity = (InventoryRequest) o;
        return Objects.equals(this.product, entity.product) &&
                Objects.equals(this.amount, entity.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, amount);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "product = " + product + ", " +
                "amount = " + amount + ")";
    }
}