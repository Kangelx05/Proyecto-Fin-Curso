package api.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.OrderDetail}
 */
public record OrderDetailResponse(Integer id, @NotNull OrderResponse order, @NotNull ProductResponse product,
                                  @NotNull Integer amount) implements Serializable {
}