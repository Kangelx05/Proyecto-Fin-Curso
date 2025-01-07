package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link api.domain.OrderDetail}
 */
public class OrderDetailRequest implements Serializable {
    @NotNull
    private final OrderRequest order;
    @NotNull
    private final ProductRequest product;
    @NotNull
    @Positive
    private final Integer amount;

    public OrderDetailRequest(OrderRequest order, ProductRequest product, Integer amount) {
        this.order = order;
        this.product = product;
        this.amount = amount;
    }

    public OrderRequest getOrder() {
        return order;
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
        OrderDetailRequest entity = (OrderDetailRequest) o;
        return Objects.equals(this.order, entity.order) &&
                Objects.equals(this.product, entity.product) &&
                Objects.equals(this.amount, entity.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, product, amount);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "order = " + order + ", " +
                "product = " + product + ", " +
                "amount = " + amount + ")";
    }
}