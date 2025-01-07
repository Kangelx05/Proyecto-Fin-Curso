package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO for {@link api.domain.Order}
 */
public class OrderResponse implements Serializable {
    private final Integer id;
    @NotNull
    private final TableResponse table;
    @NotNull
    private final Instant date;
    @NotNull
    @Size(max = 45)
    private final String state;

    public OrderResponse(Integer id, TableResponse table, Instant date, String state) {
        this.id = id;
        this.table = table;
        this.date = date;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public TableResponse getTable() {
        return table;
    }

    public Instant getDate() {
        return date;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderResponse entity = (OrderResponse) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.table, entity.table) &&
                Objects.equals(this.date, entity.date) &&
                Objects.equals(this.state, entity.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, table, date, state);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "table = " + table + ", " +
                "date = " + date + ", " +
                "state = " + state + ")";
    }
}