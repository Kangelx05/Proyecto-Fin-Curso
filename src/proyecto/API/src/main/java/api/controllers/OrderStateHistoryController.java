package api.controllers;

import api.dto.OrderStateHistoryRequest;
import api.dto.OrderStateHistoryResponse;
import api.service.OrderStateHistoryService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            operationId = "getOrderStateHistoryById",
            summary     = "Get order state history by ID",
            description = "Retrieve an order state history record by its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderStateHistoryResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(orderStateHistoryService.findById(id));
    }

    @Operation(
            operationId = "createOrderStateHistory",
            summary     = "Create a new order state history record",
            description = "Create a new order state history entry with the provided details."
    )
    @PostMapping
    public ResponseEntity<OrderStateHistoryResponse> create(@RequestBody @Valid OrderStateHistoryRequest request) {
        return ResponseEntity.ok(orderStateHistoryService.createOrderStateHistory(request));
    }

    @Operation(
            operationId = "updateOrderStateHistory",
            summary     = "Update order state history by ID",
            description = "Update the order state history record identified by its ID with new data."
    )
    @PutMapping("/{id}")
    public ResponseEntity<OrderStateHistoryResponse> update(
            @PathVariable int id,
            @RequestBody @Valid OrderStateHistoryRequest request
    ) throws Exception {
        return ResponseEntity.ok(orderStateHistoryService.updateOrderStateHistory(request, id));
    }

    @Operation(
            operationId = "deleteOrderStateHistory",
            summary     = "Delete order state history by ID",
            description = "Remove the order state history record identified by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        orderStateHistoryService.deleteOrderStateHistory(id);
        return ResponseEntity.noContent().build();
    }
}
