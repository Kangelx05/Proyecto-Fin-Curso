package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link api.domain.Order} including delivery time and total amount.
 *
 * <p>The {@code deliveredAt} field represents the moment when the order was
 * delivered to the customer.  The {@code totalAmount} field stores the sum
 * of all order detail unit prices calculated when the order is finished.
 */
public record OrderResponse(
        Integer id,
        @NotNull TableResponse table,
        @NotNull Instant date,
        @NotNull @Size(max = 45) String state,
        Instant deliveredAt,
        Float totalAmount
) implements Serializable {
}