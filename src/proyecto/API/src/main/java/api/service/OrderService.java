package api.service;

import api.domain.Order;
// DTOs and mappers are used in controllers, not the service layer
import api.domain.QrToken;
import api.repository.OrderRepository;
import api.repository.QrTokenRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final api.repository.OrderDetailRepository orderDetailRepository;
    private final api.repository.TableRepository tableRepository;
    private final QrTokenRepository qrTokenRepository;

    public OrderService(OrderRepository orderRepository,
                        api.repository.OrderDetailRepository orderDetailRepository,
                        api.repository.TableRepository tableRepository, QrTokenRepository qrTokenRepository){
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.tableRepository = tableRepository;
        this.qrTokenRepository = qrTokenRepository;
    }

    public Order findById(int id) throws Exception {
        return orderRepository.findById(id).orElseThrow();
    }

    /**
     * Persist a new order.
     *
     * @param order the domain entity to persist
     * @return the saved {@link Order}
     */
    public Order createOrder(@NotNull Order order) {
        // If the incoming order is intended to be in service, check whether
        // there is already an open order for this table.  Only one order
        // with state "IN_SERVICE" should exist per table at a time.  If such
        // an order exists, return it instead of creating a new record.
        if (order.getState() != null && order.getTable() != null &&
                "IN_SERVICE".equalsIgnoreCase(order.getState())) {
            Integer tableId = order.getTable().getId();
            if (tableId != null) {
                java.util.Optional<Order> existing = orderRepository.findByTable_IdAndState(tableId, order.getState());
                if (existing.isPresent()) {
                    return existing.get();
                }
            }
        }
        return orderRepository.save(order);
    }

    /**
     * Update an existing order with new values.
     *
     * @param updated contains the new values
     * @param id      identifier of the record to update
     * @return the updated {@link Order}
     * @throws Exception if the record does not exist
     */
    public Order updateOrder(@NotNull Order updated, int id) throws Exception {
        Order existing = orderRepository.findById(id).orElseThrow();
        existing.setDate(updated.getDate());
        existing.setState(updated.getState());
        existing.setTable(updated.getTable());
        return orderRepository.save(existing);
    }

    public void deleteOrder(int id) throws Exception {
        Order order = orderRepository.findById(id).orElseThrow();
        orderRepository.delete(order);
    }

    /**
     * Finalizes an order by calculating the total amount, recording the delivery
     * timestamp and marking the order as finished.  It also frees the
     * associated table by setting its state to "FREE".  The total amount
     * is derived by summing the unit prices of all order details linked to
     * the given order.  If no details exist, the total amount will be null.
     *
     * @param id the identifier of the order to finalize
     * @return the updated {@link Order}
     * @throws Exception if the order cannot be found
     */
    @jakarta.transaction.Transactional
    public Order finishOrder(int id) throws Exception {

        // Load the order to ensure it exists
        Order order = orderRepository.findById(id).orElseThrow();
        // Compute total from order details; null if no details
        Float total = orderDetailRepository.sumUnitPriceByOrderId(id);
        int tableId = order.getTable().getId();
        QrToken token = qrTokenRepository.findByTable_Id(tableId).orElseThrow();
        token.setStatus(QrToken.Status.EXPIRED);
        qrTokenRepository.save(token);
        // Update order fields
        order.setState("FINISHED");
        order.setDeliveredAt(java.time.Instant.now());
        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);
        // Free the associated table if present
        try {
            api.domain.Table table = order.getTable();
            if (table != null) {
                table.setState("FREE");
                tableRepository.save(table);
            }
        } catch (Exception ignored) {
            // ignore lazy loading exceptions; table may be detached
        }
        return saved;
    }
}