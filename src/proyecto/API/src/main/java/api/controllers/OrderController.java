package api.controllers;

import api.dto.OrderRequest;
import api.dto.OrderResponse;
import api.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(@PathVariable int id, @RequestBody @Valid OrderRequest request) throws Exception {
        return ResponseEntity.ok(orderService.updateOrder(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
