package api.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link api.domain.OrderDetail}
 */
public class OrderDetailResponse implements Serializable {
    private final Integer id;
    @NotNull
    private final OrderResponse order;
    @NotNull
    private final ProductResponse product;
    @NotNull
    private final Integer amount;

    public OrderDetailResponse(Integer id, OrderResponse order, ProductResponse product, Integer amount) {
        this.id = id;
        this.order = order;
        this.product = product;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public OrderResponse getOrder() {
        return order;
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
        OrderDetailResponse entity = (OrderDetailResponse) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.order, entity.order) &&
                Objects.equals(this.product, entity.product) &&
                Objects.equals(this.amount, entity.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order, product, amount);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "order = " + order + ", " +
                "product = " + product + ", " +
                "amount = " + amount + ")";
    }
}