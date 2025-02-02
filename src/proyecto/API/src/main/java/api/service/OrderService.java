package api.service;

import api.domain.Order;
import api.dto.OrderRequest;
import api.dto.OrderResponse;
import api.mappers.OrderMapper;
import api.repository.OrderRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    public OrderResponse findById(int id) throws Exception {
        Order order = orderRepository.findById(id).orElseThrow();
        return OrderMapper.toResponse(order);
    }

    public OrderResponse createOrder(@NotNull OrderRequest requestedOrder) {
        Order order = new Order();
        OrderMapper.updateOrderFromRequest(order, requestedOrder);
        Order savedOrder = orderRepository.save(order);

        return OrderMapper.toResponse(savedOrder);
    }

    public OrderResponse updateOrder(@NotNull OrderRequest requestedOrder, int id) throws Exception {
        Order order = orderRepository.findById(id).orElseThrow();
        OrderMapper.updateOrderFromRequest(order, requestedOrder);
        return OrderMapper.toResponse(orderRepository.save(order));
    }

    public void deleteOrder(int id) throws Exception {
        Order order = orderRepository.findById(id).orElseThrow();
        orderRepository.delete(order);
    }
}