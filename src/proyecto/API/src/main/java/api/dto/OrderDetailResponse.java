package api.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.OrderDetail}
 */
/**
 * DTO de respuesta para un detalle de pedido.  Incluye el nombre y precio
 * unitario del producto en lugar de retornar el objeto CardProduct completo.
 */
/**
 * Respuesta para un detalle de pedido.  Ya no se devuelve la cantidad ya
 * que cada detalle representa una única unidad del producto.  Si se
 * requieren cantidades agregadas, éstas se calculan agrupando múltiples
 * registros de OrderDetail.
 */
public record OrderDetailResponse(
        Integer id,
        @NotNull OrderResponse order,
        @NotNull String productName,
        @NotNull Float unitPrice
) implements Serializable {
}