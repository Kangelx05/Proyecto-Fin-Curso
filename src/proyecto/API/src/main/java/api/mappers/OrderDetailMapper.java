package api.mappers;

import api.domain.Order;
import api.domain.OrderDetail;
import api.domain.CardProduct;
import api.dto.OrderDetailRequest;
import api.dto.OrderDetailResponse;

public abstract class OrderDetailMapper {

    /**
     * Create a new {@link OrderDetail} given a request and pre-mapped order and product.
     * This method is retained for compatibility with older service code that
     * explicitly supplied the order and product entities.
     *
     * @param orderDetailRequest the DTO with quantity information
     * @param order              the owning {@link Order}
     * @param product            the {@link CardProduct} being ordered
     * @return a new {@link OrderDetail}
     */
    public static OrderDetail getOrderDetailFromRequest(OrderDetailRequest orderDetailRequest, Order order, CardProduct product) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        // La cantidad ya no se gestiona en OrderDetail; cada solicitud representa una unidad.
        orderDetail.setProduct(product);
        // Establecemos nombre y precio desde la solicitud
        orderDetail.setProductName(orderDetailRequest.productName());
        orderDetail.setUnitPrice(orderDetailRequest.unitPrice());
        return orderDetail;
    }

    /**
     * Create a new {@link OrderDetail} solely from the incoming request.  The
     * nested {@link api.dto.OrderRequest} and {@link api.dto.CardProductRequest} are
     * mapped into entities via their respective mappers.  A table association
     * for the order is generated with a blank waiter since the service layer
     * does not provide user resolution at this point.
     *
     * @param orderDetailRequest the DTO containing order, product and amount
     * @return a new {@link OrderDetail}
     */
    public static OrderDetail getOrderDetailFromRequest(OrderDetailRequest orderDetailRequest) {
        OrderDetail orderDetail = new OrderDetail();
        // Asociar el pedido
        if (orderDetailRequest.orderId() != null) {
            Order order = new Order();
            order.setId(orderDetailRequest.orderId());
            orderDetail.setOrder(order);
        }
        // Establecer nombre y precio unitario
        orderDetail.setProductName(orderDetailRequest.productName());
        orderDetail.setUnitPrice(orderDetailRequest.unitPrice());
        // Asociar el producto opcionalmente
        if (orderDetailRequest.productId() != null) {
            CardProduct product = new CardProduct();
            product.setId(orderDetailRequest.productId());
            orderDetail.setProduct(product);
        }
        // La cantidad ya no se gestiona en OrderDetail; cada solicitud representa una unidad.
        return orderDetail;
    }

    /**
     * Update an existing {@link OrderDetail} using values from the provided DTO.
     * Only the amount and nested associations are updated; the entity ID
     * remains unchanged.
     *
     * @param orderDetail        the existing entity to update
     * @param orderDetailRequest the DTO containing new values
     * @return the updated {@link OrderDetail}
     */
    public static OrderDetail getOrderDetailFromRequest(OrderDetail orderDetail, OrderDetailRequest orderDetailRequest) {
        if (orderDetail == null) {
            orderDetail = new OrderDetail();
        }
        // Actualizar referencia al pedido si se proporciona un ID nuevo
        if (orderDetailRequest.orderId() != null) {
            Order order = new Order();
            order.setId(orderDetailRequest.orderId());
            orderDetail.setOrder(order);
        }
        // Actualizar el producto opcional si se proporciona productId
        if (orderDetailRequest.productId() != null) {
            CardProduct product = new CardProduct();
            product.setId(orderDetailRequest.productId());
            orderDetail.setProduct(product);
        }
        // Actualizar nombre y precio unitario
        orderDetail.setProductName(orderDetailRequest.productName());
        orderDetail.setUnitPrice(orderDetailRequest.unitPrice());
        // La cantidad ya no se gestiona en OrderDetail; cada solicitud representa una unidad.
        return orderDetail;
    }

    public static OrderDetailResponse toResponse(OrderDetail orderDetail) {
        if (orderDetail == null) return null;
        return new OrderDetailResponse(
                orderDetail.getId(),
                OrderMapper.toResponse(orderDetail.getOrder()),
                orderDetail.getProductName(),
                orderDetail.getUnitPrice()
        );
    }
}

