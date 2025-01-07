package api.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link api.domain.Table}
 */
public class TableResponse implements Serializable {
    private final Integer id;
    @NotNull
    @PositiveOrZero
    private final Integer numTable;
    @NotNull
    @Positive
    private final Integer numCustomers;
    @NotNull
    private final UserResponse waiter;
    @NotNull
    @Size(max = 45)
    @NotBlank
    private final String state;

    public TableResponse(Integer id, Integer numTable, Integer numCustomers, UserResponse waiter, String state) {
        this.id = id;
        this.numTable = numTable;
        this.numCustomers = numCustomers;
        this.waiter = waiter;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public Integer getNumTable() {
        return numTable;
    }

    public Integer getNumCustomers() {
        return numCustomers;
    }

    public UserResponse getWaiter() {
        return waiter;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableResponse entity = (TableResponse) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.numTable, entity.numTable) &&
                Objects.equals(this.numCustomers, entity.numCustomers) &&
                Objects.equals(this.waiter, entity.waiter) &&
                Objects.equals(this.state, entity.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numTable, numCustomers, waiter, state);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "numTable = " + numTable + ", " +
                "numCustomers = " + numCustomers + ", " +
                "waiter = " + waiter + ", " +
                "state = " + state + ")";
    }
}