package api.controllers;

import api.dto.OrderDetailRequest;
import api.dto.OrderDetailResponse;
import api.service.OrderDetailService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-detail")
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(orderDetailService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderDetailResponse> create(@RequestBody @Valid OrderDetailRequest request) {
        return ResponseEntity.ok(orderDetailService.createOrderDetail(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> update(@PathVariable int id, @RequestBody @Valid OrderDetailRequest request) throws Exception {
        return ResponseEntity.ok(orderDetailService.updateOrderDetail(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.noContent().build();
    }
}