package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO for {@link api.domain.OrderStateHistory}
 */
public class OrderStateHistoryResponse implements Serializable {
    private final Integer id;
    @NotNull
    private final OrderResponse order;
    @Size(max = 45)
    private final String oldState;
    @NotNull
    @Size(max = 45)
    private final String newState;
    @NotNull
    private final Instant date;
    @NotNull
    private final UserResponse user;

    public OrderStateHistoryResponse(Integer id, OrderResponse order, String oldState, String newState, Instant date, UserResponse user) {
        this.id = id;
        this.order = order;
        this.oldState = oldState;
        this.newState = newState;
        this.date = date;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public OrderResponse getOrder() {
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

    public UserResponse getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderStateHistoryResponse entity = (OrderStateHistoryResponse) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.order, entity.order) &&
                Objects.equals(this.oldState, entity.oldState) &&
                Objects.equals(this.newState, entity.newState) &&
                Objects.equals(this.date, entity.date) &&
                Objects.equals(this.user, entity.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order, oldState, newState, date, user);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "order = " + order + ", " +
                "oldState = " + oldState + ", " +
                "newState = " + newState + ", " +
                "date = " + date + ", " +
                "user = " + user + ")";
    }
}