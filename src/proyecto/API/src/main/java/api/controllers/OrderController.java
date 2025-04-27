package api.controllers;

import api.dto.OrderRequest;
import api.dto.OrderResponse;
import api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(
            operationId  = "getOrderById",
            summary      = "Get order by ID",
            description  = "Retrieve an order by its unique identifier."
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable int id) throws Exception {
        return ResponseEntity.ok(orderService.findById(id));
    }

    @Operation(
            operationId  = "createOrder",
            summary      = "Create a new order",
            description  = "Create a new order with the provided details."
    )
    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @Operation(
            operationId  = "updateOrder",
            summary      = "Update order by ID",
            description  = "Update the order identified by its ID with new data."
    )
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> update(
            @PathVariable int id,
            @RequestBody @Valid OrderRequest request
    ) throws Exception {
        return ResponseEntity.ok(orderService.updateOrder(request, id));
    }

    @Operation(
            operationId  = "deleteOrder",
            summary      = "Delete order by ID",
            description  = "Remove the order identified by its ID."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) throws Exception {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
