package api.mappers;

import api.domain.OrderDetail;
import api.dto.OrderDetailRequest;
import api.dto.OrderDetailResponse;

public abstract class OrderDetailMapper {

    public static void updateOrderDetailFromRequest(OrderDetail orderDetail, OrderDetailRequest orderDetailRequest) {

        OrderMapper.updateOrderFromRequest(orderDetail.getOrder(), orderDetailRequest.order());
        orderDetail.setAmount(orderDetailRequest.amount());
        ProductMapper.updateProductFromRequest(orderDetail.getProduct(), orderDetailRequest.product());

    }

    public static OrderDetailResponse toResponse(OrderDetail orderDetail) {
        if (orderDetail == null) return null;
        return new OrderDetailResponse(
                orderDetail.getId(),
                OrderMapper.toResponse(orderDetail.getOrder()),
                ProductMapper.toResponse(orderDetail.getProduct()),
                orderDetail.getAmount()
        );

    }
}

