package api.controllers;

import api.dto.OrderStateHistoryRequest;
import api.dto.OrderStateHistoryResponse;
import api.service.OrderStateHistoryService;
import api.domain.OrderStateHistory;
import api.mappers.OrderStateHistoryMapper;
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
        return ResponseEntity.ok(OrderStateHistoryMapper.toResponse(orderStateHistoryService.findById(id)));
    }

    @Operation(
            operationId = "createOrderStateHistory",
            summary     = "Create a new order state history record",
            description = "Create a new order state history entry with the provided details."
    )
    @PostMapping
    public ResponseEntity<OrderStateHistoryResponse> create(@RequestBody @Valid OrderStateHistoryRequest request) {
        // map DTO to domain entity
        OrderStateHistory history = OrderStateHistoryMapper.getOrderStateHistoryFromRequest(request);
        OrderStateHistory saved = orderStateHistoryService.createOrderStateHistory(history);
        return ResponseEntity.ok(OrderStateHistoryMapper.toResponse(saved));
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
        OrderStateHistory updated = OrderStateHistoryMapper.getOrderStateHistoryFromRequest(request);
        OrderStateHistory saved = orderStateHistoryService.updateOrderStateHistory(updated, id);
        return ResponseEntity.ok(OrderStateHistoryMapper.toResponse(saved));
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
