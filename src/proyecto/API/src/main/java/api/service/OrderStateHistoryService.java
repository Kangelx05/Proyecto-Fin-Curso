package api.service;

import api.domain.OrderStateHistory;
// DTOs and mappers are used in controllers, not the service layer
import api.repository.OrderStateHistoryRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class OrderStateHistoryService {

    private final OrderStateHistoryRepository orderStateHistoryRepository;

    public OrderStateHistoryService(OrderStateHistoryRepository orderStateHistoryRepository){
        this.orderStateHistoryRepository = orderStateHistoryRepository;
    }

    public OrderStateHistory findById(int id) throws Exception {
        return orderStateHistoryRepository.findById(id).orElseThrow();
    }

    /**
     * Persist a new order state history entry.
     *
     * @param orderStateHistory the entity to persist
     * @return the saved {@link OrderStateHistory}
     */
    public OrderStateHistory createOrderStateHistory(@NotNull OrderStateHistory orderStateHistory) {
        return orderStateHistoryRepository.save(orderStateHistory);
    }

    /**
     * Update an existing order state history entry.
     *
     * @param updated contains the new values
     * @param id      identifier of the record to update
     * @return the updated {@link OrderStateHistory}
     * @throws Exception if the record does not exist
     */
    public OrderStateHistory updateOrderStateHistory(@NotNull OrderStateHistory updated, int id) throws Exception {
        OrderStateHistory existing = orderStateHistoryRepository.findById(id).orElseThrow();
        existing.setOldState(updated.getOldState());
        existing.setNewState(updated.getNewState());
        existing.setDate(updated.getDate());
        existing.setOrder(updated.getOrder());
        existing.setUser(updated.getUser());
        return orderStateHistoryRepository.save(existing);
    }

    public void deleteOrderStateHistory(int id) throws Exception {
        OrderStateHistory orderStateHistory = orderStateHistoryRepository.findById(id).orElseThrow();
        orderStateHistoryRepository.delete(orderStateHistory);
    }
}
