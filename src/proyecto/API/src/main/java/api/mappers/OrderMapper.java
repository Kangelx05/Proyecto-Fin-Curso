package api.mappers;

import api.domain.Order;
import api.dto.OrderRequest;
import api.dto.OrderResponse;

public abstract class OrderMapper {

    public static void updateOrderFromRequest(Order order, OrderRequest orderRequest) {

        TableMapper.updateTableFromRequest(order.getTable(), orderRequest.table());
        order.setDate(orderRequest.date());
        order.setState(orderRequest.state());

    }

    public static OrderResponse toResponse(Order order){
        if (order == null) return null;
        return new OrderResponse(
                order.getId(),
                TableMapper.toResponse(order.getTable()),
                order.getDate(),
                order.getState()
        );

    }
}
