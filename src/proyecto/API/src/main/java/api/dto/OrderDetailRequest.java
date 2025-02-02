package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.OrderDetail}
 */
public record OrderDetailRequest(@NotNull OrderRequest order, @NotNull ProductRequest product,
                                 @NotNull @Positive Integer amount) implements Serializable {

}