package api.mappers;

import api.domain.Order;
import api.domain.OrderStateHistory;
import api.domain.User;
import api.dto.OrderStateHistoryRequest;
import api.dto.OrderStateHistoryResponse;


public abstract class OrderStateHistoryMapper {

    /**
     * Create a new {@link OrderStateHistory} given a request along with
     * pre-mapped {@link Order} and {@link User} instances.  This method is
     * retained for backwards compatibility with older service layers that
     * manually resolved related entities.
     *
     * @param orderStateHistoryRequest the DTO containing the state change
     * @param order                    the associated {@link Order}
     * @param user                     the {@link User} who performed the change
     * @return a new {@link OrderStateHistory}
     */
    public static OrderStateHistory getOrderStateHistoryFromRequest(OrderStateHistoryRequest orderStateHistoryRequest, Order order, User user) {
        OrderStateHistory orderStateHistory = new OrderStateHistory();
        orderStateHistory.setNewState(orderStateHistoryRequest.newState());
        orderStateHistory.setOldState(orderStateHistoryRequest.oldState());
        orderStateHistory.setOrder(order);
        orderStateHistory.setDate(orderStateHistoryRequest.date());
        orderStateHistory.setUser(user);
        return orderStateHistory;
    }

    /**
     * Create a new {@link OrderStateHistory} solely from the incoming request.
     * The nested {@link api.dto.OrderRequest} and {@link api.dto.UserRequest}
     * will be mapped to entities using the respective mappers.  A placeholder
     * waiter is used when creating the order's table.
     *
     * @param request the DTO containing state change details
     * @return a new {@link OrderStateHistory}
     */
    public static OrderStateHistory getOrderStateHistoryFromRequest(OrderStateHistoryRequest request) {
        OrderStateHistory orderStateHistory = new OrderStateHistory();
        // map nested order request into an Order entity
        Order order = OrderMapper.getOrderFromRequest(request.order());
        orderStateHistory.setOrder(order);
        // map nested user request into a User entity
        User user = UserMapper.getUserFromRequest(request.user());
        orderStateHistory.setUser(user);
        // copy scalar values
        orderStateHistory.setOldState(request.oldState());
        orderStateHistory.setNewState(request.newState());
        orderStateHistory.setDate(request.date());
        return orderStateHistory;
    }

    /**
     * Update an existing {@link OrderStateHistory} using values from the
     * provided request. Only the scalar state fields and date are updated;
     * associations to order and user are left unchanged to be managed by
     * the service layer.
     *
     * @param orderStateHistory the existing entity to update
     * @param request           the DTO containing new values
     * @return the updated {@link OrderStateHistory}
     */
    public static OrderStateHistory updateOrderStateHistoryFromRequest(OrderStateHistory orderStateHistory, OrderStateHistoryRequest request) {
        if (orderStateHistory == null) {
            orderStateHistory = new OrderStateHistory();
        }
        orderStateHistory.setOldState(request.oldState());
        orderStateHistory.setNewState(request.newState());
        orderStateHistory.setDate(request.date());
        return orderStateHistory;
    }

    public static OrderStateHistoryResponse toResponse(OrderStateHistory orderStateHistory) {
        if (orderStateHistory == null) return null;
        return new OrderStateHistoryResponse(
                orderStateHistory.getId(),
                OrderMapper.toResponse(orderStateHistory.getOrder()),
                orderStateHistory.getOldState(),
                orderStateHistory.getNewState(),
                orderStateHistory.getDate(),
                UserMapper.toResponse(orderStateHistory.getUser())
        );
    }
}


