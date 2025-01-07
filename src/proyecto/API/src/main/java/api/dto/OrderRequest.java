package api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * DTO for {@link api.domain.Order}
 */
public class OrderRequest implements Serializable {
    @NotNull
    private final TableRequest table;
    @NotNull
    private final Instant date;
    @NotNull
    @Size(max = 45)
    @NotBlank
    private final String state;

    public OrderRequest(TableRequest table, Instant date, String state) {
        this.table = table;
        this.date = date;
        this.state = state;
    }

    public TableRequest getTable() {
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
        OrderRequest entity = (OrderRequest) o;
        return Objects.equals(this.table, entity.table) &&
                Objects.equals(this.date, entity.date) &&
                Objects.equals(this.state, entity.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(table, date, state);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "table = " + table + ", " +
                "date = " + date + ", " +
                "state = " + state + ")";
    }
}