package api.service;

import api.domain.OrderDetail;
import api.dto.OrderDetailRequest;
import api.dto.OrderDetailResponse;
import api.mappers.OrderDetailMapper;
import api.repository.OrderDetailRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailService(OrderDetailRepository orderDetailRepository){
        this.orderDetailRepository = orderDetailRepository;
    }

    public OrderDetailResponse findById(int id) throws Exception {
        OrderDetail orderDetail = orderDetailRepository.findById(id).orElseThrow();
        return OrderDetailMapper.toResponse(orderDetail);
    }

    public OrderDetailResponse createOrderDetail(@NotNull OrderDetailRequest requestedOrderDetail) {
        OrderDetail orderDetail = new OrderDetail();
        OrderDetailMapper.updateOrderDetailFromRequest(orderDetail, requestedOrderDetail);
        OrderDetail savedOrderDetail = orderDetailRepository.save(orderDetail);

        return OrderDetailMapper.toResponse(savedOrderDetail);
    }

    public OrderDetailResponse updateOrderDetail(@NotNull OrderDetailRequest requestedOrderDetail, int id) throws Exception {
        OrderDetail orderDetail = orderDetailRepository.findById(id).orElseThrow();
        OrderDetailMapper.updateOrderDetailFromRequest(orderDetail, requestedOrderDetail);
        return OrderDetailMapper.toResponse(orderDetailRepository.save(orderDetail));
    }

    public void deleteOrderDetail(int id) throws Exception {
        OrderDetail orderDetail = orderDetailRepository.findById(id).orElseThrow();
        orderDetailRepository.delete(orderDetail);
    }
}