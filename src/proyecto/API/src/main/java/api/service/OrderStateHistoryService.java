package api.service;

import api.domain.OrderStateHistory;
import api.dto.OrderStateHistoryRequest;
import api.dto.OrderStateHistoryResponse;
import api.mappers.OrderStateHistoryMapper;
import api.repository.OrderStateHistoryRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class OrderStateHistoryService {

    private final OrderStateHistoryRepository orderStateHistoryRepository;

    public OrderStateHistoryService(OrderStateHistoryRepository orderStateHistoryRepository){
        this.orderStateHistoryRepository = orderStateHistoryRepository;
    }

    public OrderStateHistoryResponse findById(int id) throws Exception {
        OrderStateHistory orderStateHistory = orderStateHistoryRepository.findById(id).orElseThrow();
        return OrderStateHistoryMapper.toResponse(orderStateHistory);
    }

    public OrderStateHistoryResponse createOrderStateHistory(@NotNull OrderStateHistoryRequest requestedOrderStateHistory) {
        OrderStateHistory orderStateHistory = new OrderStateHistory();
        OrderStateHistoryMapper.updateOrderStateHistoryFromRequest(orderStateHistory, requestedOrderStateHistory);
        OrderStateHistory savedOrderStateHistory = orderStateHistoryRepository.save(orderStateHistory);

        return OrderStateHistoryMapper.toResponse(savedOrderStateHistory);
    }

    public OrderStateHistoryResponse updateOrderStateHistory(@NotNull OrderStateHistoryRequest requestedOrderStateHistory, int id) throws Exception {
        OrderStateHistory orderStateHistory = orderStateHistoryRepository.findById(id).orElseThrow();
        OrderStateHistoryMapper.updateOrderStateHistoryFromRequest(orderStateHistory, requestedOrderStateHistory);
        return OrderStateHistoryMapper.toResponse(orderStateHistoryRepository.save(orderStateHistory));
    }

    public void deleteOrderStateHistory(int id) throws Exception {
        OrderStateHistory orderStateHistory = orderStateHistoryRepository.findById(id).orElseThrow();
        orderStateHistoryRepository.delete(orderStateHistory);
    }
}
