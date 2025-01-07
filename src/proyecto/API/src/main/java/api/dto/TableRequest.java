package api.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO for {@link api.domain.Table}
 */
public class TableRequest implements Serializable {
    @NotNull
    @PositiveOrZero
    private final Integer numTable;
    @NotNull
    @Positive
    private final Integer numCustomers;
    @NotNull
    private final UserRequest waiter;
    @NotNull
    @Size(max = 45)
    @NotBlank
    private final String state;

    public TableRequest(Integer numTable, Integer numCustomers, UserRequest waiter, String state) {
        this.numTable = numTable;
        this.numCustomers = numCustomers;
        this.waiter = waiter;
        this.state = state;
    }

    public Integer getNumTable() {
        return numTable;
    }

    public Integer getNumCustomers() {
        return numCustomers;
    }

    public UserRequest getWaiter() {
        return waiter;
    }

    public String getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableRequest entity = (TableRequest) o;
        return Objects.equals(this.numTable, entity.numTable) &&
                Objects.equals(this.numCustomers, entity.numCustomers) &&
                Objects.equals(this.waiter, entity.waiter) &&
                Objects.equals(this.state, entity.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numTable, numCustomers, waiter, state);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "numTable = " + numTable + ", " +
                "numCustomers = " + numCustomers + ", " +
                "waiter = " + waiter + ", " +
                "state = " + state + ")";
    }
}