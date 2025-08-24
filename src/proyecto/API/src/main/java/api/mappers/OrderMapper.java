package api.mappers;

import api.domain.Order;
import api.domain.Table;
import api.dto.OrderRequest;
import api.dto.OrderResponse;

public abstract class OrderMapper {

    /**
     * Create a new {@link Order} using the supplied {@link OrderRequest}
     * and an already mapped {@link Table} instance.
     *
     * <p>
     * This is the original method signature used by some callers where the
     * table entity was provided separately. It remains for backwards
     * compatibility.
     * </p>
     *
     * @param orderRequest the DTO containing date and state
     * @param table        the already mapped {@link Table}
     * @return a new {@link Order} instance
     */
    public static Order getOrderFromRequest(OrderRequest orderRequest, Table table) {
        Order order = new Order();
        // Assign the provided table reference.  When used, ensure the table
        // entity has at least its identifier set so that JPA resolves
        // the existing record rather than creating a new one.
        order.setTable(table);
        order.setDate(orderRequest.date());
        order.setState(orderRequest.state());
        return order;
    }

    /**
     * Create a new {@link Order} solely from the incoming request. A new
     * {@link Table} will be mapped from the nested {@link api.dto.TableRequest}
     * using a placeholder {@link api.domain.User} for its waiter. This is
     * sufficient for creating an order when a proper table entity is not
     * available at the service layer.
     *
     * @param orderRequest the DTO containing the table, date and state
     * @return a new {@link Order} instance
     */
    public static Order getOrderFromRequest(OrderRequest orderRequest) {
        Order order = new Order();
        // Build a table reference using only the identifier from the request.  This avoids
        // creating a new table record and instead links the order to the existing table
        // in the database.  The table entity will be resolved by JPA when the order is
        // persisted.
        if (orderRequest.tableId() != null) {
            Table table = new Table();
            table.setId(orderRequest.tableId());
            order.setTable(table);
        }
        order.setDate(orderRequest.date());
        order.setState(orderRequest.state());
        return order;
    }

    /**
     * Mutate an existing {@link Order} instance from an incoming request.
     * Only scalar fields (date and state) are updated. The table
     * association is left untouched because the service layer should
     * handle entity relationships.
     *
     * @param order        the entity to update
     * @param orderRequest the DTO containing new values
     * @return the updated {@link Order}
     */
    public static Order getOrderFromRequest(Order order, OrderRequest orderRequest) {
        if (order == null) {
            order = new Order();
        }
        // If a new table identifier is supplied, update the table reference
        if (orderRequest.tableId() != null) {
            Table table = new Table();
            table.setId(orderRequest.tableId());
            order.setTable(table);
        }
        order.setDate(orderRequest.date());
        order.setState(orderRequest.state());
        return order;
    }

    public static OrderResponse toResponse(Order order) {
        if (order == null) return null;
        return new OrderResponse(
                order.getId(),
                TableMapper.toResponse(order.getTable()),
                order.getDate(),
                order.getState(),
                order.getDeliveredAt(),
                order.getTotalAmount()
        );
    }
}
