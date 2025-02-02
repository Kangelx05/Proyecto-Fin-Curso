package api.controllers;

import api.dto.OrderStateHistoryRequest;
import api.dto.OrderStateHistoryResponse;
import api.service.OrderStateHistoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-state-history")
public class OrderStateHistoryController {
    private final OrderStateHistoryService orderStateHistoryService;

    public OrderStateHistoryController(OrderStateHistoryService orderStateHistoryService) {
        this.orderStateHistoryService = orderStateHistoryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderStateHistoryResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(orderStateHistoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<OrderStateHistoryResponse> create(@RequestBody @Valid OrderStateHistoryRequest request) {
        return ResponseEntity.ok(orderStateHistoryService.createOrderStateHistory(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderStateHistoryResponse> update(@PathVariable int id, @RequestBody @Valid OrderStateHistoryRequest request) throws Exception {
        return ResponseEntity.ok(orderStateHistoryService.updateOrderStateHistory(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        orderStateHistoryService.deleteOrderStateHistory(id);
        return ResponseEntity.noContent().build();
    }
}
