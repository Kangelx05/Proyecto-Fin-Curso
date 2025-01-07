package api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO for {@link api.domain.OrderStateHistory}
 */
public class OrderStateHistoryRequest implements Serializable {
    @NotNull
    @NotBlank
    private final OrderRequest order;
    @Size(max = 45)
    private final String oldState;
    @NotNull
    @NotBlank
    @Size(max = 45)
    private final String newState;
    @NotNull
    private final Instant date;
    @NotNull
    private final UserRequest user;

    public OrderStateHistoryRequest(OrderRequest order, String oldState, String newState, Instant date, UserRequest user) {
        this.order = order;
        this.oldState = oldState;
        this.newState = newState;
        this.date = date;
        this.user = user;
    }

    public OrderRequest getOrder() {
        return order;
    }

    public String getOldState() {
        return oldState;
    }

    public String getNewState() {
        return newState;
    }

    public Instant getDate() {
        return date;
    }

    public UserRequest getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStateHistoryRequest entity = (OrderStateHistoryRequest) o;
        return Objects.equals(this.order, entity.order) &&
                Objects.equals(this.oldState, entity.oldState) &&
                Objects.equals(this.newState, entity.newState) &&
                Objects.equals(this.date, entity.date) &&
                Objects.equals(this.user, entity.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(order, oldState, newState, date, user);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "order = " + order + ", " +
                "oldState = " + oldState + ", " +
                "newState = " + newState + ", " +
                "date = " + date + ", " +
                "user = " + user + ")";
    }
}