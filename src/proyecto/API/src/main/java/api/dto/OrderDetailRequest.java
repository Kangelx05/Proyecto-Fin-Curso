package api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.io.Serializable;

/**
 * DTO for {@link api.domain.OrderDetail}
 */
/**
 * DTO para la creación o actualización de un detalle de pedido.  Se almacenan
 * el nombre y el precio del producto en el momento de la venta, de modo que
 * los registros no dependan de la existencia del producto en la carta.  El
 * productId es opcional y solo se utiliza para referencias estadísticas.
 */
/**
 * Solicitud para crear o actualizar un detalle de pedido.  A partir de esta
 * versión cada OrderDetail representa únicamente una unidad de producto,
 * por lo que no se envía una cantidad.  En lugar de ello, el cliente
 * enviará múltiples solicitudes de OrderDetailRequest cuando se requiera
 * más de una unidad.
 */
public record OrderDetailRequest(
        @NotNull Integer orderId,
        @NotNull String productName,
        @NotNull Float unitPrice,
        Integer productId
) implements Serializable {
}